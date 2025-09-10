package org.toop.eventbus;

import org.toop.server.Server;

/**
 * Events that are used in the GlobalEventBus class.
 */
public class Events {

    public static class ServerEvents {
        /**
         * Triggers when a command is sent to a server.
         */
        public record OnCommand(Server.Command command, String[] args, Server.Message result) {}

        /**
         * Triggers on changing the server backend.
         */
        public record OnChangingServerBackend(Server.ServerBackend backend) {}

    }

}
