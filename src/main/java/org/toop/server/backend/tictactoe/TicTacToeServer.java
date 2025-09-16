package org.toop.server.backend.tictactoe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.server.backend.tictactoe.game.TicTacToe;
import org.toop.server.backend.TcpServer;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TicTacToeServer extends TcpServer {

    protected static final Logger logger = LogManager.getLogger(TicTacToeServer.class);
    /**
     * Map of gameId -> Game instances
     */
    private final Map<String, TicTacToe> games = new ConcurrentHashMap<>();

    public TicTacToeServer(int port) throws IOException {
        super(port);
    }

    @Override
    public void run() {
        try {
            logger.info("Tic tac toe server listening on port {}", this.port);

            while (isRunning()) {
                Socket clientSocket = this.serverSocket.accept();
                logger.info("Connected to client: {}", clientSocket.getInetAddress());

                new Thread(() -> this.startWorkers(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String newGame(String playerA, String playerB) {
        logger.info("Creating a new game: {} vs {}", playerA, playerB);
        String gameId = UUID.randomUUID().toString();
        TicTacToe game = new TicTacToe(playerA, playerB);
        this.games.put(gameId, game);
        logger.info("Created a new game: {}. {} vs {}", gameId, playerA, playerB);
        return gameId;
    }

    public void runGame(String gameId) {
        TicTacToe game = this.games.get(gameId);
        game.run();
        logger.info("Running game: {}, players: {}", gameId, game.getPlayers());
    }

    public void endGame(String gameId) {
        TicTacToe game = this.games.get(gameId);
        this.games.remove(gameId);
        logger.info("Ended game: {}", gameId);
        // TODO: Multithreading, close game in a graceful matter, etc.
    }

    private static class ParsedCommand {
        public TicTacToeServerCommand command;
        public ArrayList<Object> arguments;
        public boolean isValidCommand;
        public TicTacToeServerMessage returnMessage;
        public String errorMessage;
        public String originalCommand;

        ParsedCommand(String receivedCommand) {

            if (receivedCommand.isEmpty()) {
                this.command = null;
                this.arguments = null;
                this.isValidCommand = false;
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
                this.returnMessage = TicTacToeServerMessage.ERR;
                this.errorMessage = "The received command is empty or couldn't be split";
                this.originalCommand = receivedCommand;
                return;
            };

            TicTacToeServerCommand commandEnum = TicTacToeServerCommand.getCommand(segments[0]);
            switch (commandEnum) {
                case MOVE -> {
                    if (segments.length == 2 && !segments[1].isEmpty()) {
                        this.command = commandEnum;
                        this.arguments = new ArrayList<Object>(1);
                        this.arguments.add(segments[1]);
                        this.returnMessage = TicTacToeServerMessage.OK;
                        this.isValidCommand = true;
                        this.errorMessage = null;
                        this.originalCommand = receivedCommand;
                        return;
                    }
                }
                case FORFEIT -> {
                        this.command = commandEnum;
                        this.arguments = null;
                        this.returnMessage = TicTacToeServerMessage.OK;
                        this.isValidCommand = true;
                        this.errorMessage = null;
                        this.originalCommand = receivedCommand;
                        return;
                }
                case MESSAGE -> {
                    if (segments.length == 3 && !segments[2].isEmpty()) {
                        this.command = commandEnum;
                        this.arguments = new ArrayList<Object>(2);
                        this.arguments.add(segments[2]);
                        this.returnMessage = TicTacToeServerMessage.OK;
                        this.isValidCommand = true;
                        this.errorMessage = null;
                        this.originalCommand = receivedCommand;
                        return;
                    }
                }
                case BYE, DISCONNECT, LOGOUT, QUIT, EXIT -> {
                    this.command = commandEnum;
                    this.arguments = null;
                    this.returnMessage = TicTacToeServerMessage.OK;
                    this.isValidCommand = true;
                    this.errorMessage = null;
                    this.originalCommand = receivedCommand;
                    return;
                }
            }
            this.command = command;
            this.arguments = arguments;
        }
    }

    private ParsedCommand parseCommand(String command) {
        return null;
    }

}
