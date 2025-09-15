package org.toop.server.backend.tictactoe;

import java.util.EnumSet;

public enum TicTacToeServerCommand {
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

    private static final EnumSet<TicTacToeServerCommand> VALID_COMMANDS = EnumSet.of(
        TicTacToeServerCommand.LOGIN,     TicTacToeServerCommand.LOGOUT,     TicTacToeServerCommand.EXIT,
        TicTacToeServerCommand.QUIT,      TicTacToeServerCommand.DISCONNECT, TicTacToeServerCommand.BYE,
        TicTacToeServerCommand.GET,       TicTacToeServerCommand.SUBSCRIBE,  TicTacToeServerCommand.MOVE,
        TicTacToeServerCommand.CHALLENGE, TicTacToeServerCommand.FORFEIT,
        TicTacToeServerCommand.MESSAGE,   TicTacToeServerCommand.HELP
    );

    public static EnumSet<TicTacToeServerCommand> getValidCommands() {
        return VALID_COMMANDS;
    }

    // TODO: Garbage code.

    /**
     * @param command Checks if string is a valid command.
     * @return returns a boolean if string is a valid command.
     */
    public static boolean isValid(String command) {
        try {
            TicTacToeServerCommand.valueOf(command.toUpperCase());
            return true;
        } catch (IllegalArgumentException err) {
            return false;
        }
    }

    // TODO: Return something better
    /**
     * @param command Converts a string into a TicTacToeServerCommand.
     * @return returns a TicTacToeServerCommand enum.
     */
    public static TicTacToeServerCommand getCommand(String command) {
        if (isValid(command)) {
            TicTacToeServerCommand.valueOf(command.toUpperCase());
            return TicTacToeServerCommand.valueOf(command.toUpperCase());
        }
        return null;
    }

}
