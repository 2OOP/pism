package org.toop.server.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.server.backend.tictactoe.ParsedCommand;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class TcpServer implements Runnable {

    protected static final Logger logger = LogManager.getLogger(TcpServer.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    public final BlockingQueue<String> receivedQueue = new LinkedBlockingQueue<>();
    public final BlockingQueue<ParsedCommand> commandQueue = new LinkedBlockingQueue<>();
    public final BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();
    public final Map<Socket, String> knownPlayers = new HashMap<>();
    public final Map<String, String> playersGames = new HashMap<>();
    public final int WAIT_TIME = 500; // MS
    public final int RETRY_ATTEMPTS = 3;

    protected int port;
    protected ServerSocket serverSocket = null;
    private boolean running = true;

    public TcpServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void run() {
        try {
            logger.info("Server listening on port {}", port);

            while (running) {
                Socket clientSocket = this.serverSocket.accept();
                logger.info("Connected to client: {}", clientSocket.getInetAddress());

                new Thread(() -> this.startWorkers(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runGame() {}

    public void endGame() {}

    public void newGame() {}

    protected String sendServerMessage() {
        try { return sendQueue.poll(this.WAIT_TIME, TimeUnit.MILLISECONDS); }
        catch (InterruptedException e) {
            logger.error("Interrupted", e);
            return null;
        }
    }

    protected ParsedCommand getNewestCommand() {
        try {
            String rec = receivedQueue.poll(this.WAIT_TIME, TimeUnit.MILLISECONDS);
            if (rec != null) {
                return new ParsedCommand(rec);
            }
        }
        catch (InterruptedException e) {
            logger.error("Interrupted", e);
            return null;
        }
        return null;
    }
//
//    protected void sendMessage(String message) throws InterruptedException {
//        sendQueue.put(message);
//    }

    protected void startWorkers(Socket clientSocket) {
        running = true;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            this.executor.submit(() -> this.inputLoop(in));
            this.executor.submit(() -> this.outputLoop(out));
        } catch (Exception e) {
            logger.error("Server could not start, {}", e);
        }

    }

    private void stopWorkers() {
        this.running = false;
        this.receivedQueue.clear();
        this.sendQueue.clear();
        this.executor.shutdownNow();
    }

    private void inputLoop(BufferedReader in) {

        logger.info("Starting {} connection read", this.port);
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                logger.info("Received: '{}'", message);
                if (!message.isEmpty()) {
                    String finalMessage = message;
                    new Thread(() -> {
                        for (int i = 0; i < this.RETRY_ATTEMPTS; i++) {
                            if (this.receivedQueue.offer(finalMessage)) break;
                            try {
                                sleep(this.WAIT_TIME);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                }
            }
        } catch (IOException e) {
            logger.error("Error reading from server", e);
        } finally {
            try {
                this.serverSocket.close();
                logger.info("Client disconnected. {}", this.port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void outputLoop(PrintWriter out) {
        logger.info("Starting {} connection write", this.port);
        try {
            while (this.running) {
                String send = this.sendQueue.poll(this.WAIT_TIME, TimeUnit.MILLISECONDS);
                if (send != null) {
                    out.println(send);
                    logger.info("Sent message from server {}: '{}'", this.port, send);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        stopWorkers();
        logger.info("sendQueue:     {}", this.sendQueue.toString());
        logger.info("receivedQueue: {}", this.receivedQueue.toString());
    }
}