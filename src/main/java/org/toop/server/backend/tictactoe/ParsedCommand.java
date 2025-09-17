package org.toop.server.backend.tictactoe;

import java.util.ArrayList;

public class ParsedCommand {
    public TicTacToeServerCommand command;
    public ArrayList<Object> arguments;
    public boolean isValidCommand;
    public boolean isServerCommand;
    public TicTacToeServerMessage returnMessage;
    public String errorMessage;
    public String originalCommand;

    public ParsedCommand(String receivedCommand) {

        if (receivedCommand.isEmpty()) {
            this.command = null;
            this.arguments = null;
            this.isValidCommand = false;
            this.isServerCommand = true;
            this.returnMessage = TicTacToeServerMessage.ERR;
            this.errorMessage = "The received command is empty";
            this.originalCommand = receivedCommand;
            return;
        }

        String[] segments = receivedCommand.split(" ");
        if (segments[0].isEmpty()) {
            this.command = null;
            this.arguments = null;
            this.isValidCommand = false;
            this.isServerCommand = true;
            this.returnMessage = TicTacToeServerMessage.ERR;
            this.errorMessage = "The received command is empty or couldn't be split";
            this.originalCommand = receivedCommand;
            return;
        }

        TicTacToeServerCommand commandEnum = TicTacToeServerCommand.getCommand(segments[0]);

        switch (commandEnum) {
            case MOVE -> {
                if (segments.length == 2 && !segments[1].isEmpty()) {
                    this.command = commandEnum;
                    this.arguments = new ArrayList<>(1);
                    this.arguments.add(segments[1]);
                    this.returnMessage = TicTacToeServerMessage.OK;
                    this.isValidCommand = true;
                    this.isServerCommand = false;
                    this.errorMessage = null;
                    this.originalCommand = receivedCommand;
                    return;
                }
            }
            case MESSAGE -> {
                if (segments.length == 3 && !segments[2].isEmpty()) {
                    this.command = commandEnum;
                    this.arguments = new ArrayList<>(2);
                    this.arguments.add(segments[2]);
                    this.returnMessage = TicTacToeServerMessage.OK;
                    this.isValidCommand = true;
                    this.isServerCommand = true;
                    this.errorMessage = null;
                    this.originalCommand = receivedCommand;
                    return;
                }
            }
            case CHALLENGE -> {
                if (!segments[1].isEmpty() && segments[1].equals("accept") &&
                        !segments[2].isEmpty()) {
                    this.command = commandEnum;
                    this.arguments = new ArrayList<>(2);
                    this.arguments.add(segments[1]);
                    this.arguments.add(segments[2]); // TODO: Needs to be a number.
                    this.returnMessage = TicTacToeServerMessage.OK;
                    this.isValidCommand = true;
                    this.isServerCommand = true;
                    this.errorMessage = null;
                    this.originalCommand = receivedCommand;
                    return;
                } else {
                    this.command = commandEnum;
                    this.arguments = null;
                    this.returnMessage = TicTacToeServerMessage.ERR;
                    this.isValidCommand = false;
                    this.isServerCommand = true;
                    this.errorMessage = "The challenge was not parsable";
                    this.originalCommand = receivedCommand;
                    return;
                }
            }
            case LOGIN -> { // TODO: Challenge needs to accept different game types later.
                if (!segments[1].isEmpty()) {
                    this.command = commandEnum;
                    this.arguments = new ArrayList<>(1);
                    this.arguments.add(segments[1]);
                    this.returnMessage = TicTacToeServerMessage.OK;
                    this.isValidCommand = true;
                    this.isServerCommand = true;
                    this.errorMessage = null;
                    this.originalCommand = receivedCommand;
                    return;
                } else {
                    this.command = commandEnum;
                    this.arguments = null;
                    this.returnMessage = TicTacToeServerMessage.ERR;
                    this.isValidCommand = false;
                    this.isServerCommand = true;
                    this.errorMessage = "The received name is empty or couldn't be understood";
                    this.originalCommand = receivedCommand;
                    return;
                }
            }
//                case GET -> { // TODO: Get needs to accept different game types later. And get the players
//
//                }
            case BYE, DISCONNECT, LOGOUT, QUIT, EXIT, FORFEIT, SUBSCRIBE -> {
                this.command = commandEnum;
                this.arguments = null;
                this.returnMessage = TicTacToeServerMessage.OK;
                this.isValidCommand = true;
                this.isServerCommand = true;
                this.errorMessage = null;
                this.originalCommand = receivedCommand;
                return;
            }
            case null, default -> {
                this.command = null;
                this.arguments = null;
                this.returnMessage = TicTacToeServerMessage.ERR;
                this.isValidCommand = false;
                this.isServerCommand = true;
                this.errorMessage = segments[0] + " is not a supported command";
                this.originalCommand = receivedCommand;
                return;
            }
        }
    }
//
//    public ParsedCommand parseCommand(String command) {
//        return null;
//    }

}