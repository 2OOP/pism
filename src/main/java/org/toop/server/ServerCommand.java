package org.toop.server;

import java.util.EnumSet;

public enum ServerCommand {
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
    HELP;

    private static final EnumSet<ServerCommand> VALID_COMMANDS = EnumSet.of(
        ServerCommand.LOGIN,     ServerCommand.LOGOUT,     ServerCommand.EXIT,
        ServerCommand.QUIT,      ServerCommand.DISCONNECT, ServerCommand.BYE,
        ServerCommand.GET,       ServerCommand.SUBSCRIBE,  ServerCommand.MOVE,
        ServerCommand.CHALLENGE, ServerCommand.FORFEIT,
        ServerCommand.MESSAGE,   ServerCommand.HELP
    );

    public static EnumSet<ServerCommand> getValidCommands() {
        return VALID_COMMANDS;
    }

    // TODO: Garbage code.

    /**
     * @param command Checks if string is a valid command.
     * @return returns a boolean if string is a valid command.
     */
    public static boolean isValid(String command) {
        try {
            ServerCommand.valueOf(command.toUpperCase());
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
    public static ServerCommand getCommand(String command) {
        if (isValid(command)) {
            ServerCommand.valueOf(command.toUpperCase());
            return ServerCommand.valueOf(command.toUpperCase());
        }
        return null;
    }

}
