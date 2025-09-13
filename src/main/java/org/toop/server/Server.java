package org.toop.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.*;
import org.toop.server.backend.local.Local;
import org.toop.server.backend.remote.Remote;
import org.toop.server.backend.remote.TcpClient;

import java.io.IOException;
import java.net.UnknownHostException;
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
    IBackend backend;
    BlockingQueue<String> commandQueue;
    // TODO Reconnect and keep trying to connect.

    public Server(String set_backend, String set_ip, String set_port) {
        ip = set_ip;
        port = set_port;
        this.setBackend(set_backend);
        this.initEvents();
        this.commandQueue = new LinkedBlockingQueue<>();
    }

    public IBackend getBackend() {
        return backend;
    }

    /**
     * @param backend The backend to change to.
     */
    public void setBackend(ServerBackend backend) {
        if (backend == ServerBackend.LOCAL) {
            this.backend = new Local();
            GlobalEventBus.post(new Events.ServerEvents.OnChangingServerBackend(ServerBackend.LOCAL));
        }
        else {
            this.backend = new Remote();
            GlobalEventBus.post(new Events.ServerEvents.OnChangingServerBackend(ServerBackend.REMOTE));
        }
    }

    public void setBackend(String backend) {
        if (backend.equalsIgnoreCase("REMOTE")) {
            this.backend = new Remote();
            GlobalEventBus.post(new Events.ServerEvents.OnChangingServerBackend(ServerBackend.REMOTE));
        }
        else {
            this.backend = new Local();
            GlobalEventBus.post(new Events.ServerEvents.OnChangingServerBackend(ServerBackend.LOCAL));
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
                "Server {ip: \"%s\", port: \"%s\", backend: \"%s\"}",
                ip, port, backend
        );
    }

    private void initEvents() {
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.command.class, event
                -> this.sendCommandByString(event.command(), event.args()));
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.changeServerIp.class, event
                -> this.setIp(event.ip()));
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.changeServerPort.class, event
                -> this.setPort(event.port()));
    }

    /**
     * DO NOT USE, USE START INSTEAD.
     */
    public void run() {
        try {
            TcpClient client = new TcpClient(this.getIp(), Integer.parseInt(this.getPort())); // TODO Parsing to int is unsafe
            theRemoteServerTimeline(client);
        } catch (UnknownHostException | InterruptedException e) { // TODO Better error handling.
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void theRemoteServerTimeline(TcpClient client) throws InterruptedException { // TODO: Rename
        sleep(1000); // Just wait, because why not

        new Thread(() -> {
            try {
                while (true) {
                    String received = client.readLine(); // blocks until a line is available
                    if (received != null) {
                        logger.info("Received: '{}'", received);
                        GlobalEventBus.post(new Events.ServerEvents.ReceivedMessage(received));
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error("Error reading from server", e);
            }
        }).start();

        new Thread(() -> {
            try {
                while (true) {
                    String command = commandQueue.take(); // blocks until a command is available
                    client.sendMessage(command);
                    logger.info("Sent command: '{}'", command);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    /**
     *
     * Starts a server thread.
     *
     * @param backend The backend to use {remote, local}
     * @param ip The address of the server to contact.
     * @param port The port of the server.
     */
    public static void start(String backend, String ip, String port) {
        try {
            new Server(backend, ip, port).start();
        } catch (IllegalArgumentException e) {
            new Server("REMOTE", "127.0.0.1", "5001").start(); // TODO: Doesn't do anything.
        }
    }

}

