package org.toop.server.backend.tictactoe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.server.backend.tictactoe.game.TicTacToe;
import org.toop.server.backend.TcpServer;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
                new Thread(this::gameManagerThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected ParsedCommand getNewestCommand() {
        try {
            String rec = receivedQueue.poll(this.WAIT_TIME, TimeUnit.MILLISECONDS);
            if (rec != null) {
                return new ParsedCommand(rec);
            }
        }
        catch (InterruptedException e) {
            logger.error("Interrupted", e);
            return null;
        }
        return null;
    }

    public void gameManagerThread() {
        while (true) { // TODO: Very heavy on thread
            try {
                wait(250);
            } catch (InterruptedException e) {
                logger.error("Interrupted", e);
            }
            ParsedCommand command = getNewestCommand();
            if (command != null && !command.isServerCommand) {
                TicTacToe testGame = games.values().iterator().next(); // TODO: Is to get first for testing, must be done a different way later.
                testGame.addCommandToQueue(command);
                logger.info("Added command to the game queue: {}", command);
                return;
            }
        }
    }

    public String newGame(String playerA, String playerB) {
        logger.info("Creating a new game: {} vs {}", playerA, playerB);
        String gameId = UUID.randomUUID().toString();
        TicTacToe game = new TicTacToe(playerA, playerB);
        this.games.put(gameId, game);
//        this.knownPlayers.put(sockA, playerA); // TODO: For remembering players and validation.
//        this.knownPlayers.put(sockB, playerB);
//        this.playersGames.put(playerA, gameId);
//        this.playersGames.put(playerB, gameId);
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
//        this.knownPlayers.put(sockA, playerA); // TODO: Remove players when game is done.
//        this.knownPlayers.put(sockB, playerB);
//        this.playersGames.put(playerA, gameId);
//        this.playersGames.put(playerB, gameId);
        logger.info("Ended game: {}", gameId);
        // TODO: Multithreading, close game in a graceful matter, etc.
    }

}
