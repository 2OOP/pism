package org.toop.eventbus;

import org.toop.server.backend.tictactoe.TicTacToeServer;
import org.toop.server.backend.tictactoe.TicTacToeServerCommand;
import org.toop.server.Server;
import org.toop.core.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;

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

        /**
         * BLOCKING
         * Requests all active connections. The result is returned via the provided CompletableFuture.
         *
         * @param future List of all connections in string form.
         */
        public record RequestsAllConnections(CompletableFuture<String> future) {}

        /**
         * BLOCKING
         * Requests all active servers. The result is returned via the provided CompletableFuture.
         *
         * @param future List of all servers in string form.
         */
        public record RequestsAllServers(CompletableFuture<String> future) {}

        /**
         * Forces closing all active connections immediately.
         */
        public record ForceCloseAllConnections() {}

        /**
         * Forces closing all active servers immediately.
         */
        public record ForceCloseAllServers() {}

        /**
         * Requests starting a server with a specific port and game type.
         *
         * @param port The port to open the server.
         * @param gameType Either "tictactoe" or ...
         */
        public record StartServer(String port, String gameType) {}

        /**
         * BLOCKING
         * Requests starting a server with a specific port and game type, and returns a CompletableFuture
         * that completes when the server has started.
         *
         * @param port The port to open the server.
         * @param gameType Either "tictactoe" or ...
         * @param future The uuid of the server.
         */
        public record StartServerRequest(String port, String gameType, CompletableFuture<String> future) {}

        /**
         * Represents a server that has successfully started.
         *
         * @param uuid The unique identifier of the server.
         * @param port The port the server is listening on.
         */
        public record ServerStarted(String uuid, String port) {}

        /**
         * BLOCKING
         * Requests creation of a TicTacToe game on a specific server.
         *
         * @param serverUuid The unique identifier of the server where the game will be created.
         * @param playerA The name of the first player.
         * @param playerB The name of the second player.
         * @param future The game UUID when the game is created.
         */
        public record CreateTicTacToeGameRequest(String serverUuid, String playerA, String playerB, CompletableFuture<String> future) {}

        /**
         * Requests running a TicTacToe game on a specific server.
         *
         * @param serverUuid The unique identifier of the server.
         * @param gameUuid The UUID of the game to run.
         */
        public record RunTicTacToeGame(String serverUuid, String gameUuid) {}

        /**
         * Requests ending a TicTacToe game on a specific server.
         *
         * @param serverUuid The UUID of the server the game is running on.
         * @param gameUuid The UUID of the game to end.
         */
        public record EndTicTacToeGame(String serverUuid, String gameUuid) {}

        /**
         *
         * Triggers starting a server connection.
         *
         * @param ip The IP address of the server to connect to.
         * @param port The port of the server to connect to.
         */
        public record StartConnection(String ip, String port) {}

        /**
         * BLOCKING
         * Triggers starting a server connection and returns a future.
         *
         * @param ip The IP address of the server to connect to.
         * @param port The port of the server to connect to.
         * @param future Returns the UUID of the connection, when connection is established.
         */
        public record StartConnectionRequest(String ip, String port, CompletableFuture<String> future) {}

//        public record StartGameConnectionRequest(String ip, String port, CompletableFuture<String> future) {}

        /**
         * BLOCKING
         * Triggers starting a server connection and returns a future.
         *
         * @param ip The IP address of the server to connect to.
         * @param port The port of the server to connect to.
         * @param future The CompletableFuture that will complete when the connection is established.
         */
        public record ConnectionEstablished(Object connectionId, String ip, String port) {}

        /**
         * Triggers sending a command to a server.
         *
         * @param connectionId The UUID of the connection to send the command on.
         * @param args The command arguments.
         */
        public record SendCommand(String connectionId, String... args) { }

        /**
         * WIP
         * Triggers when a command is sent to a server.
         *
         * @param command The TicTacToeServer instance that executed the command.
         * @param args The command arguments.
         * @param result The result returned from executing the command.
         */
        public record OnCommand(TicTacToeServer command, String[] args, String result) {}

        /**
         * Triggers when the server client receives a message.
         *
         * @param ConnectionUuid The UUID of the connection that received the message.
         * @param message The message received.
         */
        public record ReceivedMessage(String ConnectionUuid, String message) {}

        /**
         * Triggers on changing the server IP.
         *
         * @param ip The new IP address.
         */
        public record OnChangingServerIp(String ip) {}

        /**
         * Triggers on changing the server port.
         *
         * @param port The new port.
         */
        public record OnChangingServerPort(String port) {}

        /**
         * Triggers reconnecting to a previous address.
         *
         * @param connectionId The identifier of the connection being reconnected.
         */
        public record Reconnect(Object connectionId) {}

        /**
         * Triggers changing connection to a new address.
         *
         * @param connectionId The identifier of the connection being changed.
         * @param ip The new IP address.
         * @param port The new port.
         */
        public record ChangeConnection(Object connectionId, String ip, String port) {}

        /**
         * Triggers when the server couldn't connect to the desired address.
         *
         * @param connectionId The identifier of the connection that failed.
         */
        public record CouldNotConnect(Object connectionId) {}

        /**
         * WIP
         * Triggers when a connection closes.
         *
         */
        public record ClosedConnection() {}

    }

    public static class EventBusEvents {

    }

    public static class WindowEvents {
		/**
		* Triggers when the window wants to quit.
		*/
		public record OnQuitRequested() {}

		/**
		* Triggers when the window is resized.
		*/
		public record OnResize(Window.Size size) {}

		/**
		* Triggers when the mouse is moved within the window.
		*/
		public record OnMouseMove(int x, int y) {}

		/**
		* Triggers when the mouse is clicked within the window.
		*/
		public record OnMouseClick(int button) {}

		/**
		* Triggers when the mouse is released within the window.
		*/
		public record OnMouseRelease(int button) {}
    }

    public static class TttEvents {

    }

    public static class AiTttEvents {

    }
}
