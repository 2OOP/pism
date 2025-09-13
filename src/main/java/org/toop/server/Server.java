package org.toop.server;

import org.toop.Main;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.TcpClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends Thread {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public enum ServerBackend {
        LOCAL,
        REMOTE,
    }

    String ip;
    String port;
    BlockingQueue<String> commandQueue; // TODO, add a way to close it, for when reconnecting.
    TcpClient tcpClient;
    volatile boolean running = false;
    Thread tcpIn;
    Thread tcpOut;

    public Server(String set_ip, String set_port) {
        ip = set_ip;
        port = set_port;
        this.initEvents();
        this.commandQueue = new LinkedBlockingQueue<>();
        try {
            this.tcpClient = new TcpClient(this.getIp(), Integer.parseInt(this.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        return ip;
    }

    /**
     * @param ip The ip to change to.
     */
    public void setIp(String ip) {

        this.ip = ip;
        GlobalEventBus.post(new Events.ServerEvents.OnChangingServerIp(ip));

    }

    public String getPort() {
        return port;
    }


    /**
     * @param port The port to change to.
     */
    public void setPort(String port) {

        this.port = port;
        GlobalEventBus.post(new Events.ServerEvents.OnChangingServerPort(port));

    }

    /**
     *
     * Sends a command to the server.
     *
     * @param command The command to send to the server.
     * @param args The arguments for the command.
     */
    private void sendCommandByString(String command, String... args) {

        if (!ServerCommand.isValid(command)) {
            logger.error("Invalid command: {}", command);
            return;
        }

        if (!running) {
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

    @Override
    public String toString() {

        return String.format(
                "Server {ip: \"%s\", port: \"%s\"}",
                ip, port
        );

    }

    private void initEvents() {

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.command.class, event
                -> this.sendCommandByString(event.command(), event.args()));
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.Reconnect.class, event
                -> {
                    try {
                        this.reconnect(); // TODO: Terrible error handling and code. Needs cleanup.
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.ChangeConnection.class, event
                -> {
                    try {
                        this.connect(event.ip(), event.port()); // TODO: Terrible error handling and code. Needs cleanup.
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

    }

    private void connection() throws InterruptedException { // TODO: Rename

        sleep(1000); // Just wait, because why not

        this.tcpIn = new Thread(() -> {
            try {
                while (this.running) {
                    String received = this.tcpClient.readLine(); // blocks until a line is available
                    if (received != null) {
                        logger.info("Received: '{}'", received);
                        GlobalEventBus.post(new Events.ServerEvents.ReceivedMessage(received));
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error("Error reading from server", e);
                try {
                    this.tcpClient.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        this.tcpOut = new Thread(() -> {
            try {
                while (this.running) {
                    String command = commandQueue.take(); // blocks until a command is available
                    this.tcpClient.sendMessage(command);
                    logger.info("Sent command: '{}'", command);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                try {
                    tcpClient.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        });

        this.tcpIn.start();
        this.tcpOut.start();

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
    public void connect(String ip, String port) throws IOException, InterruptedException {

        if (running) {
            this.close();
        }

        this.ip = ip;
        this.port = port;

        running = true;

        this.tcpClient = new TcpClient(ip, Integer.parseInt(port));
        this.connection();

    }

    /**
     *
     * Reconnects to previous address.
     *
     * @throws IOException wip
     * @throws InterruptedException wip
     */
    public void reconnect() throws IOException, InterruptedException {
        this.connect(this.ip, this.port);
    }

    /**
     *
     * Close connection to server.
     *
     * @throws IOException wip
     * @throws InterruptedException wip
     */
    public void close() throws IOException, InterruptedException {

        this.commandQueue.clear();
        running = false;
        // Thread.currentThread().interrupt();

        this.tcpClient.closeSocket();

        this.tcpIn.interrupt();
        this.tcpOut.interrupt();
        this.tcpIn.join();
        this.tcpOut.join();

    }

    /**
     * DO NOT USE, USE START INSTEAD.
     */
    public void run() {

        try {
            this.reconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *
     * Starts a server thread.
     *
     * @param ip The address of the server to contact.
     * @param port The port of the server.
     */
    public static void start(String ip, String port) {

        try {
            new Server(ip, port).start();
        } catch (IllegalArgumentException e) {
            new Server("127.0.0.1", "5001").start(); // TODO: Doesn't do anything.
        }
    }

}

