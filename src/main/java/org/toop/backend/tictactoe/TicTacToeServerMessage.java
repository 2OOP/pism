package org.toop.backend.tictactoe;

import java.util.EnumSet;

public enum TicTacToeServerMessage {
    OK,
    ERR,
    SVR;

    private static final EnumSet<TicTacToeServerMessage> VALID_COMMANDS =
            EnumSet.of(
                    TicTacToeServerMessage.OK,
                    TicTacToeServerMessage.ERR,
                    TicTacToeServerMessage.SVR);

    public static EnumSet<TicTacToeServerMessage> getValidCommands() {
        return VALID_COMMANDS;
    }

    // TODO: Garbage code.
    /**
     * @param command Checks if string is a valid command.
     * @return returns a boolean if string is a valid command.
     */
    public static boolean isValid(String command) {
        try {
            TicTacToeServerMessage.valueOf(command.toUpperCase());
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
    public static TicTacToeServerMessage getCommand(String command) {
        if (isValid(command)) {
            return TicTacToeServerMessage.valueOf(command.toUpperCase());
        }
        return null;
    }
}
