package org.toop.eventbus;

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
         * Triggers sending a command to a server.
         */
        public record command(String command, String... args) {}

        /**
         * Triggers when a command is sent to a server.
         */
        public record OnCommand(org.toop.server.ServerCommand command, String[] args, String result) {}

        /**
         * Triggers when the server client receives a message.
         */
        public record ReceivedMessage(String message) {}

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
        public record Reconnect() {}

        /**
         * Triggers changing connection to a new address.
         */
        public record ChangeConnection(String ip, String port) { }

        /**
         * Triggers when a cell is clicked in one of the game boards.
         */
        public record CellClicked(int cell) {}
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
