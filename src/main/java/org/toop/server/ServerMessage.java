package org.toop.server;

import java.util.EnumSet;

public enum ServerMessage {
    OK,
    ERR,
    SVR;

    private static final EnumSet<ServerMessage> VALID_COMMANDS = EnumSet.of(
        ServerMessage.OK,     ServerMessage.ERR,     ServerMessage.SVR
    );

    public static EnumSet<ServerMessage> getValidCommands() {
        return VALID_COMMANDS;
    }

    // TODO: Garbage code.
    /**
     * @param command Checks if string is a valid command.
     * @return returns a boolean if string is a valid command.
     */
    public static boolean isValid(String command) {
        try {
            ServerMessage.valueOf(command.toUpperCase());
            return true;
        } catch (IllegalArgumentException err) {
            return false;
        }
    }

    // TODO: Return something better
    /**
     * @param command Converts a string into a ServerCommand.
     * @return returns a ServerCommand enum.
     */
    public static ServerMessage getCommand(String command) {
        if (isValid(command)) {
            ServerMessage.valueOf(command.toUpperCase());
            return ServerMessage.valueOf(command.toUpperCase());
        }
        return null;
    }

}
