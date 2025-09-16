package org.toop.server.frontend;

import org.toop.Main;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.tictactoe.ServerCommand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.*;

public final class ServerConnection implements Runnable {

    private static final Logger logger = LogManager.getLogger(ServerConnection.class);

    private final BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    String ip;
    String port;
    TcpClient tcpClient;
    volatile boolean running = false;

    public ServerConnection(String ip, String port) {
        this.ip = ip;
        this.port = port;
        this.initEvents();
    }

    public String getIp() {
        return this.ip;
    }

    public String getPort() {
        return this.port;
    }

    /**
     *
     * Sends a command to the server.
     *
     * @param command The command to send to the server.
     * @param args The arguments for the command.
     */
    public void sendCommandByString(String command, String... args) {
        if (!ServerCommand.isValid(command)) {
            logger.error("Invalid command: {}", command);
            return;
        }

        System.out.println();

        if (!this.running) {
            logger.warn("Server has been stopped");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
            if (args[i].isEmpty()) {
                throw new IllegalArgumentException("Empty argument"); // TODO: Error handling, just crashes atm.
            }
        }

        String[] fullCommand = new String[args.length + 1];
        fullCommand[0] = command;
        System.arraycopy(args, 0, fullCommand, 1, args.length);
        command = String.join(" ", fullCommand);

        this.commandQueue.add(command);
        logger.info("Command '{}' added to the queue", command);
    }

    private void initEvents() {
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.Reconnect.class, _ -> {
                    try {
                        this.reconnect();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
        });

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.ChangeConnection.class, event -> {
                    try {
                        this.connect(event.ip(), event.port());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
        });
    }

    private void startWorkers() {
        running = true;
        this.executor.submit(this::inputLoop);
        this.executor.submit(this::outputLoop);
    }

    private void stopWorkers() {
        this.running = false;
        this.commandQueue.clear();

        if (this.tcpClient != null) {
            try {
                this.tcpClient.closeSocket();
            } catch (IOException e) {
                logger.warn("Error closing client socket", e);
            }
        }

        this.executor.shutdownNow();
    }

    private void inputLoop() {
        logger.info("Starting {}:{} connection read", this.ip, this.port);
        try {
            while (running) {
                String received = tcpClient.readLine(); // blocks
                if (received != null) {
                    logger.info("Received: '{}'", received);
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            logger.error("Error reading from server", e);
        }
    }

    private void outputLoop() {
        logger.info("Starting {}:{} connection write", this.ip, this.port);
        try {
            while (this.running) {
                String command = this.commandQueue.poll(500, TimeUnit.MILLISECONDS);
                if (command != null) {
                    this.tcpClient.sendMessage(command);
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
     * @throws IOException wip
     * @throws InterruptedException wip
     */
    public void connect(String ip, String port) throws IOException {
        if (this.running) {
            this.closeConnection();
        }

        this.ip = ip;
        this.port = port;
        this.tcpClient = new TcpClient(ip, Integer.parseInt(port));

        this.startWorkers();
    }

    /**
     *
     * Reconnects to previous address.
     *
     * @throws IOException wip
     */
    public void reconnect() throws IOException {
        this.connect(this.ip, this.port);
    }

    /**
     *
     * Close connection to server.
     *
     */
    public void closeConnection() {
        this.stopWorkers();
        logger.info("Server connection closed");
    }

    /**
     * DO NOT USE, USE startNew INSTEAD.
     */
    @Override
    public void run() {
        try {
            reconnect();
        } catch (IOException e) {
            logger.error("Initial connection failed", e);
        }
    }

    /**
     *
     * Starts a server thread.
     *
     * @param ip The address of the server to contact.
     * @param port The port of the server.
     */
    public static ServerConnection startNew(String ip, String port) {
        ServerConnection serverConnection = new ServerConnection(ip, port);
        new Thread(serverConnection).start();
        return serverConnection;
    }

    @Override
    public String toString() {
        return String.format(
                "Server {ip: \"%s\", port: \"%s\", running: %s}",
                this.ip, this.port, this.running
        );
    }

}

