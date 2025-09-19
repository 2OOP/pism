package org.toop.server.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.server.backend.tictactoe.ParsedCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Lightweight, thread-pool based TCP server base class.
 *
 * Responsibilities:
 * - accept sockets
 * - hand off socket I/O to connectionExecutor (pooled threads)
 * - provide thread-safe queues (receivedQueue / sendQueue) to subclasses
 *
 * Notes:
 * - Subclasses should consume receivedQueue (or call getNewestCommand()) and
 *   use sendQueue to send messages to all clients (or per-client, if implemented).
 */
public abstract class TcpServer implements Runnable {

    protected static final Logger logger = LogManager.getLogger(TcpServer.class);

    // Executor used for per-connection I/O tasks (reading/writing)
    protected final ExecutorService connectionExecutor = Executors.newCachedThreadPool();

    // Shared queues for subclasses / consumers
    public final BlockingQueue<String> receivedQueue = new LinkedBlockingQueue<>();
    public final BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();

    // Association for sockets -> player ids
    public final Map<Socket, String> knownPlayers = new ConcurrentHashMap<>();
    public final Map<String, String> playersGames = new ConcurrentHashMap<>();

    // tunables
    public final int WAIT_TIME = 500; // ms used by poll-based methods
    public final int RETRY_ATTEMPTS = 3;

    protected final int port;
    protected final ServerSocket serverSocket;
    private volatile boolean running = true;

    public TcpServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    public boolean isRunning() {
        return running;
    }

    /**
     * Default run: accept connections and hand off to connectionExecutor.
     * Subclasses overriding run() should still call startWorkers(Socket) for each accepted socket.
     */
    @Override
    public void run() {
        logger.info("Server listening on port {}", port);
        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Accepted connection from {}", clientSocket.getRemoteSocketAddress());
                // hand off to pool to manage I/O for this socket
                connectionExecutor.submit(() -> startWorkers(clientSocket));
            }
        } catch (IOException e) {
            if (running) {
                logger.error("Accept failed", e);
            } else {
                logger.info("Server socket closed, stopping acceptor");
            }
        }
    }

    /**
     * Listen/Write workers for an accepted client socket.
     * This method submits two tasks to the connectionExecutor:
     *  - inputLoop: reads lines and enqueues them to receivedQueue
     *  - outputLoop: polls sendQueue and writes messages to the client
     *
     * Note: This is a simple model where sendQueue is global; if you need per-client
     * send-queues, adapt this method to use one per socket.
     */
    protected void startWorkers(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Input task: read lines and put them on receivedQueue
            Runnable inputTask = () -> {
                logger.info("Starting read loop for {}", clientSocket.getRemoteSocketAddress());
                try {
                    String line;
                    while (running && (line = in.readLine()) != null) {
                        if (line.isEmpty()) continue;
                        logger.debug("Received from {}: {}", clientSocket.getRemoteSocketAddress(), line);

                        boolean offered = false;
                        for (int i = 0; i < RETRY_ATTEMPTS && !offered; i++) {
                            try {
                                // Use offer to avoid blocking indefinitely; adapt timeout/policy as needed
                                offered = this.receivedQueue.offer(line, 200, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }

                        if (!offered) {
                            logger.warn("Backpressure: dropping line from {}: {}", clientSocket.getRemoteSocketAddress(), line);
                            // Policy choice: drop, notify, or close connection. We drop here.
                        }
                    }
                } catch (IOException e) {
                    logger.info("Connection closed by remote: {}", clientSocket.getRemoteSocketAddress());
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException ignored) {}
                    logger.info("Stopped read loop for {}", clientSocket.getRemoteSocketAddress());
                }
            };

            // Output task: poll global sendQueue and write to this specific client.
            // NOTE: With a single global sendQueue, every message is sent to every connected client.
            // If you want per-client sends, change this to use per-client queue map.
            Runnable outputTask = () -> {
                logger.info("Starting write loop for {}", clientSocket.getRemoteSocketAddress());
                try {
                    while (running && !clientSocket.isClosed()) {
                        String msg = sendQueue.poll(WAIT_TIME, TimeUnit.MILLISECONDS);
                        if (msg != null) {
                            out.println(msg);
                            out.flush();
                            logger.debug("Sent to {}: {}", clientSocket.getRemoteSocketAddress(), msg);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Writer interrupted for {}", clientSocket.getRemoteSocketAddress());
                } catch (Exception e) {
                    logger.error("Writer error for {}: {}", clientSocket.getRemoteSocketAddress(), e.toString());
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException ignored) {}
                    logger.info("Stopped write loop for {}", clientSocket.getRemoteSocketAddress());
                }
            };

            // Input and Output mappings
            connectionExecutor.submit(inputTask);
            connectionExecutor.submit(outputTask);

        } catch (IOException e) {
            logger.error("Could not start workers for client: {}", e.toString());
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    /**
     * Convenience: wrapper to obtain the latest command (non-blocking poll).
     * Subclasses can use this, but for blocking behavior consider using receivedQueue.take()
     */
    protected ParsedCommand getNewestCommand() {
        try {
            String rec = receivedQueue.poll(WAIT_TIME, TimeUnit.MILLISECONDS);
            if (rec != null) return new ParsedCommand(rec);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Interrupted while polling receivedQueue", e);
        }
        return null;
    }

    /**
     * Stop server and cleanup executors/sockets.
     */
    public void stop() {
        running = false;

        try {
            serverSocket.close();
        } catch (IOException ignored) {}

        connectionExecutor.shutdownNow();

        logger.info("TcpServer stopped. receivedQueue size={}, sendQueue size={}",
                receivedQueue.size(), sendQueue.size());
    }
}