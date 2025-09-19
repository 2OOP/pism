package org.toop.frontend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.*;

public final class ServerConnection extends TcpClient implements Runnable {

    private static final Logger logger = LogManager.getLogger(ServerConnection.class);

    private final BlockingQueue<String> receivedQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    String uuid;
    volatile boolean running = false;

    public ServerConnection(String uuid, String ip, String port) throws IOException {
        super(ip, Integer.parseInt(port)); // TODO: Verify if port is integer first, to avoid crash.
        this.uuid = uuid;
        this.initEvents();
    }

    /**
     *
     * Sends a command to the server.
     *
     * @param args The arguments for the command.
     */
    public void sendCommandByString(String... args) {
//        if (!TicTacToeServerCommand.isValid(command)) {
//            logger.error("Invalid command: {}", command);
//            return;
//        } // TODO: DO I CARE?

//        if (!this.running) {
//            logger.warn("Server has been stopped");
//            return;
//        } // TODO: Server not running

        String command = String.join(" ", args);

        this.sendQueue.add(command);
        logger.info("Command '{}' added to the queue", command); // TODO: Better log, which uuid?
    }

    private void addReceivedMessageToQueue(String message) {
        try {
            receivedQueue.put(message);
        } catch (InterruptedException e) {
            logger.error("{}", e); // TODO: Make more informative
        }
    }

    private void initEvents() {}

    private void startWorkers() {
        running = true;
        this.executor.submit(this::inputLoop);
        this.executor.submit(this::outputLoop);
    }

    private void stopWorkers() {
        this.running = false;
        this.sendQueue.clear();
            try {
                this.closeSocket();
            } catch (IOException e) {
                logger.warn("Error closing client socket", e); // TODO: Better log
            }

        this.executor.shutdownNow();
    }

    private void inputLoop() {
        logger.info("Starting {}:{} connection read", this.serverAddress, this.serverPort);
        try {
            while (running) {
                String received = this.readLine(); // blocks
                if (received != null) {
                    logger.info("Connection: {} received: '{}'", this.uuid, received);
                    // this.addReceivedMessageToQueue(received); // TODO: Will never go empty
                    GlobalEventBus.post(new Events.ServerEvents.ReceivedMessage(this.uuid, received)); // TODO: mb change
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Error reading from server", e);
        }
    }

    private void outputLoop() {
        logger.info("Starting {}:{} connection write", this.serverAddress, this.serverPort);
        try {
            while (this.running) {
                String command = this.sendQueue.poll(500, TimeUnit.MILLISECONDS);
                if (command != null) {
                    this.sendMessage(command);
                    logger.info("Sent command: '{}'", command);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.error("Error sending command", e);
        }
    }

    /**
     *
     * Connect to a new server.
     *
     * @param ip The ip to connect to.
     * @param port The port to connect to.
     */
    public void connect(InetAddress ip, int port) {
        if (this.running) {
            this.closeConnection(); // Also stops workers.
        }

        this.serverAddress = ip;
        this.serverPort = port;

        this.startWorkers();
    }

    /**
     *
     * Reconnects to previous address.
     *
     * @throws IOException wip
     */
    public void reconnect() throws IOException {
        this.connect(this.serverAddress, this.serverPort);
    }

    /**
     *
     * Close connection to server.
     *
     */
    public void closeConnection() {
        this.stopWorkers();
        logger.info("Closed connection: {}, to server {}:{}", this.uuid, this.serverAddress, this.serverPort);
    }

    @Override
    public void run() {
        try {
            reconnect();
        } catch (IOException e) {
            logger.error("Initial connection failed", e);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Server {ip: \"%s\", port: \"%s\", running: %s}",
                this.serverAddress, this.serverPort, this.running
        );
    }

}

