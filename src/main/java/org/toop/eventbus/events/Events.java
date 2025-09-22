package org.toop.eventbus.events;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.toop.core.Window;

/** Events that are used in the GlobalEventBus class. */
public class Events implements IEvents {

    /**
     * WIP, DO NOT USE!
     *
     * @param eventName
     * @param args
     * @return
     * @throws Exception
     */
    public static Object get(String eventName, Object... args) throws Exception {
        Class<?> clazz = Class.forName("org.toop.eventbus.events.Events$ServerEvents$" + eventName);
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        Constructor<?> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(args);
    }

    /**
     * WIP, DO NOT USE!
     *
     * @param eventCategory
     * @param eventName
     * @param args
     * @return
     * @throws Exception
     */
    public static Object get(String eventCategory, String eventName, Object... args)
            throws Exception {
        Class<?> clazz =
                Class.forName("org.toop.eventbus.events.Events$" + eventCategory + "$" + eventName);
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        Constructor<?> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(args);
    }

    /**
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
         * BLOCKING Requests all active servers. The result is returned via the provided
         * CompletableFuture.
         *
         * @param future List of all servers in string form.
         */
        public record RequestsAllServers(CompletableFuture<String> future) {}

        /** Forces closing all active servers immediately. */
        public record ForceCloseAllServers() {}

        /**
         * Requests starting a server with a specific port and game type.
         *
         * @param port The port to open the server.
         * @param gameType Either "tictactoe" or ...
         */
        public record StartServer(int port, String gameType) {}

        /**
         * BLOCKING Requests starting a server with a specific port and game type, and returns a
         * CompletableFuture that completes when the server has started.
         *
         * @param port The port to open the server.
         * @param gameType Either "tictactoe" or ...
         * @param future The uuid of the server.
         */
        public record StartServerRequest(
                int port, String gameType, CompletableFuture<String> future) {}

        /**
         * Represents a server that has successfully started.
         *
         * @param uuid The unique identifier of the server.
         * @param port The port the server is listening on.
         */
        public record ServerStarted(String uuid, int port) {}

        /**
         * BLOCKING Requests creation of a TicTacToe game on a specific server.
         *
         * @param serverUuid The unique identifier of the server where the game will be created.
         * @param playerA The name of the first player.
         * @param playerB The name of the second player.
         * @param future The game UUID when the game is created.
         */
        public record CreateTicTacToeGameRequest(
                String serverUuid,
                String playerA,
                String playerB,
                CompletableFuture<String> future) {}

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

        //        public record StartGameConnectionRequest(String ip, String port,
        // CompletableFuture<String> future) {}

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
        public record OnChangingServerPort(int port) {}

        /** Triggers when a cell is clicked in one of the game boards. */
        public record CellClicked(int cell) {}
    }

    public static class EventBusEvents {}

    public static class WindowEvents {
        /** Triggers when the window wants to quit. */
        public record OnQuitRequested() {}

        /** Triggers when the window is resized. */
        public record OnResize(Window.Size size) {}

        /** Triggers when the mouse is moved within the window. */
        public record OnMouseMove(int x, int y) {}

        /** Triggers when the mouse is clicked within the window. */
        public record OnMouseClick(int button) {}

        /** Triggers when the mouse is released within the window. */
        public record OnMouseRelease(int button) {}
    }

    public static class TttEvents {}

    public static class AiTttEvents {}
}
