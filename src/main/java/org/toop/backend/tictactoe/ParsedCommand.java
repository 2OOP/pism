package org.toop.backend.tictactoe;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParsedCommand {
    private static final Logger logger = LogManager.getLogger(ParsedCommand.class);

    public TicTacToeServerCommand command;
    public ArrayList<Object> arguments;
    public boolean isValidCommand;
    public boolean isServerCommand;
    public TicTacToeServerMessage returnMessage;
    public String errorMessage;
    public String originalCommand;
    public String gameId;
    public String player;

    public ParsedCommand(String receivedCommand) {

        if (receivedCommand.isEmpty()) {
            logger.info("Received empty command");
            this.gameId = null;
            this.player = null;
            this.command = null;
            this.arguments = null;
            this.isValidCommand = false;
            this.isServerCommand = true;
            this.returnMessage = TicTacToeServerMessage.ERR;
            this.errorMessage = "The received command is empty";
            this.originalCommand = receivedCommand;
            return;
        }

        // Case-insensitive regex to match: game_id {id} player {name}
        Pattern pattern =
                Pattern.compile(
                        "(?i)\\bgame[_]?id\\s+(\\S+)\\s+player\\s+(\\S+)",
                        Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(receivedCommand);

        String tempGameId = null;
        String tempPlayer = null;
        String tempPayload = receivedCommand;

        if (matcher.find()) {
            tempGameId = matcher.group(1); // first capture group → game_id
            tempPlayer = matcher.group(2); // second capture group → player
            // Remove the matched part from the original command
            tempPayload = matcher.replaceFirst("").trim();
        }

        this.gameId = tempGameId;
        this.player = tempPlayer;
        receivedCommand = tempPayload;

        logger.info("Received gameId: {}", gameId);
        logger.info("Received player: {}", player);
        logger.info("Received command: {}", receivedCommand);

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
            case CREATE_GAME -> {
                if (segments.length == 3 && !segments[1].isEmpty() && !segments[2].isEmpty()) {
                    this.command = commandEnum;
                    this.arguments = new ArrayList<>(2);
                    this.arguments.add(segments[1]);
                    this.arguments.add(segments[2]);
                    this.returnMessage = TicTacToeServerMessage.OK;
                    this.isValidCommand = true;
                    this.isServerCommand = true;
                    this.errorMessage = null;
                    this.originalCommand = receivedCommand;
                    return;
                }
            }
            case END_GAME, START_GAME -> {
                if (segments.length == 2 && !segments[1].isEmpty()) {
                    this.command = commandEnum;
                    this.arguments = new ArrayList<>(1);
                    this.arguments.add(segments[1]);
                    this.returnMessage = TicTacToeServerMessage.OK;
                    this.isValidCommand = true;
                    this.isServerCommand = true;
                    this.errorMessage = null;
                    this.originalCommand = receivedCommand;
                    return;
                }
            }
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
                if (!segments[1].isEmpty()
                        && segments[1].equals("accept")
                        && !segments[2].isEmpty()) {
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
            //                case GET -> { // TODO: Get needs to accept different game types later.
            // And get the players
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

    @Override
    public String toString() {
        return this.originalCommand; // TODO: Maybe return more info.
    }

    //
    //    public ParsedCommand parseCommand(String command) {
    //        return null;
    //    }

}
