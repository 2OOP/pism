package org.toop.eventbus;

import org.toop.server.backend.tictactoe.TicTacToeServer;
import org.toop.server.backend.tictactoe.TicTacToeServerCommand;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Events that are used in the GlobalEventBus class.
 */
public class Events implements IEvents {

    /**
     *
     * WIP, DO NOT USE!
     *
     * @param eventName
     * @param args
     * @return
     * @throws Exception
     */
    public static Object get(String eventName, Object... args) throws Exception {
        Class<?> clazz = Class.forName("org.toop.eventbus.Events$ServerEvents$" + eventName);
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        Constructor<?> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(args);
    }

    /**
     *
     * WIP, DO NOT USE!
     *
     * @param eventCategory
     * @param eventName
     * @param args
     * @return
     * @throws Exception
     */
    public static Object get(String eventCategory, String eventName, Object... args) throws Exception {
        Class<?> clazz = Class.forName("org.toop.eventbus.Events$" + eventCategory + "$" + eventName);
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        Constructor<?> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(args);
    }

    /**
     *
     * WIP, DO NOT USE!
     *
     * @param eventName
     * @param args
     * @return
     * @throws Exception
     */
    public static Object get2(String eventName, Object... args) throws Exception {
        // Fully qualified class name
        String className = "org.toop.server.backend.Events$ServerEvents$" + eventName;

        // Load the class
        Class<?> clazz = Class.forName(className);

        // Build array of argument types
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }

        // Get the constructor
        Constructor<?> constructor = clazz.getConstructor(paramTypes);

        // Create a new instance
        return constructor.newInstance(args);
    }

    public static class ServerEvents {

        public record RequestsAllConnections(CompletableFuture<String> future) {}

        public record RequestsAllServers(CompletableFuture<String> future) {}

        public record ForceCloseAllConnections() {}

        public record ForceCloseAllServers() {}

        public record StartServer(String port, String gameType) {}

        public record StartServerRequest(String port, String gameType, CompletableFuture<String> future) {}

        public record ServerStarted(String uuid, String port) {}

        public record CreateTicTacToeGameRequest(String serverUuid, String playerA, String playerB, CompletableFuture<String> future) {}

        public record RunTicTacToeGame(String serverUuid, String gameUuid) {}

        public record EndTicTacToeGame(String serverUuid, String gameUuid) {}

        /**
         *
         * Triggers starting a server connection.
         *
         * @param ip
         * @param port
         */
        public record StartConnection(String ip, String port) {}

        /**
         * Triggers starting a server connection, returns a future.
         * WARNING: This is a blocking operation.
         *
         * @param ip
         * @param port
         * @param future
         */
        public record StartConnectionRequest(String ip, String port, CompletableFuture<String> future) {}

//        public record StartGameConnectionRequest(String ip, String port, CompletableFuture<String> future) {}

        /**
         * Triggers when a connection to a server is established.
         *
         * @param connectionId
         * @param ip
         * @param port
         */
        public record ConnectionEstablished(Object connectionId, String ip, String port) {}

        /**
         * Triggers sending a command to a server.
         */
        public record SendCommand(String connectionId, String... args) { }

        /**
         * Triggers when a command is sent to a server.
         */
        public record OnCommand(TicTacToeServer command, String[] args, String result) {}

        /**
         * Triggers when the server client receives a message.
         */
        public record ReceivedMessage(String ConnectionUuid, String message) {}

        /**
         * Triggers on changing the server ip.
         */
        public record OnChangingServerIp(String ip) {}

        /**
         * Triggers on changing the server port.
         */
        public record OnChangingServerPort(String port) {}

        /**
         * Triggers reconnecting to previous address.
         */
        public record Reconnect(Object connectionId) {}

        /**
         * Triggers changing connection to a new address.
         */
        public record ChangeConnection(Object connectionId, String ip, String port) {}

        /**
         * Triggers when the server couldn't connect to the desired address.
         */
        public record CouldNotConnect(Object connectionId) {}

        public record ClosedConnection() {}

    }

    public static class EventBusEvents {

    }

    public static class WindowEvents {

    }

    public static class TttEvents {

    }

    public static class AiTttEvents {

    }
}
