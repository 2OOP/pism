package org.toop.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;
import org.toop.eventbus.EventRegistry;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.backend.*;
import java.util.EnumSet;

public class Server extends Thread {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public enum ServerBackend {
        LOCAL,
        REMOTE,
    }

    public enum Command {
        /**
         * Login, "username"
         */
        LOGIN,
        /**
         * Logout, "username"
         */
        LOGOUT,
        EXIT,
        QUIT,
        DISCONNECT,
        BYE,
        GET,
        SUBSCRIBE,
        MOVE,
        CHALLENGE,
        FORFEIT,
        MESSAGE,
        HELP,
    }

    private static final EnumSet<Command> VALID_COMMANDS = EnumSet.of(
        Command.LOGIN, Command.LOGOUT, Command.EXIT, Command.QUIT,
        Command.DISCONNECT, Command.BYE, Command.GET, Command.SUBSCRIBE,
        Command.MOVE, Command.CHALLENGE, Command.FORFEIT,
        Command.MESSAGE, Command.HELP
    );

    public enum Message {
        OK,
        ERR,
        SVR,
    }

    String ip;
    String port;
    IBackend backend;

    public Server(ServerBackend set_backend, String set_ip, String set_port) {
        ip = set_ip;
        port = set_port;
        setBackend(set_backend);
        this.initEvents();
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

    private Message sendCommandString(String sentence) {
        return Message.OK;
    }

    private boolean isCommandValid(Command command) {
        return VALID_COMMANDS.contains(command);
    }

    /**
     * Sends a command to the server.
     *
     * @param command the command to execute
     * @return a Message indicating success or error
     */
    public Message sendCommand(Command command) {
        if (!isCommandValid(command)) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }
        Message result = sendCommandString(command.toString());

        GlobalEventBus.post(new Events.ServerEvents.OnCommand(command, new String[0], result));

        return sendCommandString(command.toString());
    }

    /**
     * Sends a command to the server.
     *
     * @param command the command to execute
     * @param args command arguments.
     * @return a Message indicating success or error
     */
    public Message sendCommand(Command command, String... args) {
        if (!isCommandValid(command)) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
            if (args[i].isEmpty()) {
                throw new IllegalArgumentException("Empty argument");
            }
        }

        String[] fullCommand = new String[args.length + 1];
        fullCommand[0] = command.toString();
        System.arraycopy(args, 0, fullCommand, 1, args.length);

        Message result = sendCommandString(String.join(" ", fullCommand));

        GlobalEventBus.post(new Events.ServerEvents.OnCommand(command, args, result));

        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "Server {ip: \"%s\", port: \"%s\", backend: \"%s\"}",
                ip, port, backend
        );
    }

    private void initEvents() {
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.command.class, e -> this.sendCommand(e.command(), e.args()));
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.changeServerIp.class, e -> this.setIp(e.ip()));
        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.changeServerPort.class, e -> this.setPort(e.port()));
    }

    public void run() {
        while (true) {
            logger.info("Ik ben Bas, hallo");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

