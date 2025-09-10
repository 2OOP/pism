package org.toop.eventbus;

import org.toop.server.Server;

/**
 * Events that are used in the GlobalEventBus class.
 */
public class Events implements IEvents {

    public static class ServerEvents {

        /**
         * Triggers sending a command to a server.
         */
        public record command(Server.Command command, String... args) {}

        /**
         * Triggers when a command is sent to a server.
         */
        public record OnCommand(Server.Command command, String[] args, Server.Message result) {}

        /**
         * Triggers on changing the server backend.
         */
        public record OnChangingServerBackend(Server.ServerBackend backend) {}

        /**
         * Triggers changing the server ip.
         */
        public record changeServerIp(String ip) {}

        /**
         * Triggers on changing the server ip.
         */
        public record OnChangingServerIp(String ip) {}

        /**
         * Triggers changing the server port.
         */
        public record changeServerPort(String port) {}

        /**
         * Triggers on changing the server port.
         */
        public record OnChangingServerPort(String port) {}

    }

}
