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

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Server extends Thread {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public enum ServerBackend {
        LOCAL,
        REMOTE,
    }

    public enum Message {
        OK,
        ERR,
        SVR,
    }

    String ip;
    String port;
    IBackend backend;
    List<String> commandQueue;

    public Server(ServerBackend set_backend, String set_ip, String set_port) {
        ip = set_ip;
        port = set_port;
        setBackend(set_backend);
        this.initEvents();
        this.commandQueue = new LinkedList<>();
    }

    public IBackend getBackend() {
        return backend;
    }

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
        GlobalEventBus.post(new Events.ServerEvents.OnChangingServerIp(ip));
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
        GlobalEventBus.post(new Events.ServerEvents.OnChangingServerPort(port));
    }

    private void sendCommandByString(String command, String... args) {
        if (!ServerCommand.isValid(command)) {
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

        this.commandQueue.add(Arrays.toString(fullCommand)); // TODO Dunno if correct

        logger.info("Command {} added to the queue", Arrays.toString(fullCommand));

    }

//    /**
//     * Sends a command to the server.
//     *
//     * @param command the command to execute
//     * @return a Message indicating success or error
//     */
//    public void sendCommand(ServerCommand command) {
//
//        Message result = Message.OK;
//
//        this.commandQueue.add(command.toString());
//
//        GlobalEventBus.post(new Events.ServerEvents.OnCommand(command, new String[0], result));
//
//        return result;
//    }

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

    public void run() {
        try {
            TcpClient client = new TcpClient(this.getIp(), Integer.parseInt(this.getPort())); // TODO This is unsafe
            theRemoteServerTimeline(client);
        } catch (UnknownHostException | InterruptedException e) { // TODO Better error handling.
            throw new RuntimeException(e);
        }
    }

    private void theRemoteServerTimeline(TcpClient client) throws InterruptedException {
        while (true) {
            sleep(500); // 1s delay to not overload server.
            if (!commandQueue.isEmpty()) {
                String command = commandQueue.removeFirst();
                logger.info("Sending command: {}", command);
                try {
                    client.sendMessage(command); // TODO: Will block.
                    client.readLine(); // TODO Does this need to wait?
                }  catch (Exception e) {
                    // TODO: Error handling.
                }
            }
        }
    }

    public static void start(String backend, String ip, String port) {
        try {
            new Server(ServerBackend.valueOf(backend.toUpperCase()), ip, port).start();
        } catch (IllegalArgumentException e) {
            new Server(ServerBackend.LOCAL, "127.0.0.1", "5001").start();
        }
    }

}

