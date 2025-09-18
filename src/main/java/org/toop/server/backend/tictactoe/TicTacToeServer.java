package org.toop.server.backend.tictactoe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.server.backend.TcpServer;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class TicTacToeServer extends TcpServer {

    protected static final Logger logger = LogManager.getLogger(TicTacToeServer.class);

    private final ExecutorService connectionExecutor = Executors.newCachedThreadPool(); // socket I/O
    private final ExecutorService dispatcherExecutor;
    private final ExecutorService forwarderExecutor = Executors.newSingleThreadExecutor();

    private final BlockingQueue<ParsedCommand> incomingCommands;
    private final Map<String, TicTacToe> games = new ConcurrentHashMap<>();

    public TicTacToeServer(int port) throws IOException {
        super(port);

        int dispatchers = Math.max(2, Runtime.getRuntime().availableProcessors());
        this.dispatcherExecutor = Executors.newFixedThreadPool(dispatchers + 1); // TODO: Magic number for forwardMessages
        this.incomingCommands = new LinkedBlockingQueue<>(5_000);

        forwarderExecutor.submit(this::forwardLoop);

        for (int i = 0; i < dispatchers; i++) {
            dispatcherExecutor.submit(this::dispatchLoop);
        }
    }

    @Override
    public void run() {
        try {
            logger.info("TicTacToe server listening on port {}", this.port);

            while (isRunning()) {
                Socket clientSocket = this.serverSocket.accept();
                logger.info("Connected to client: {}", clientSocket.getInetAddress());

                connectionExecutor.submit(() -> this.startWorkers(clientSocket));
            }
        } catch (IOException e) {
            logger.error("I/O error in server run loop", e);
        }
    }

    /**
     * Forwards raw messages from TcpServer.receivedQueue into ParsedCommand objects.
     */
    private void forwardLoop() {
        logger.info("Forwarder loop started");
        try {
            while (isRunning()) {
                String raw = this.receivedQueue.take(); // blocks
                logger.info("Received command: {}", raw);
                try {
                    ParsedCommand pc = new ParsedCommand(raw);
                    this.incomingCommands.put(pc); // blocks if full
                } catch (Exception e) {
                    logger.warn("Invalid message ignored: {}", raw, e);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Forwarder loop interrupted");
        }
    }

    /**
     * Dispatches parsed commands into the game logic.
     */
    private void dispatchLoop() {
        logger.info("Dispatcher thread started");
        try {
            while (isRunning()) {
                ParsedCommand command = this.incomingCommands.take(); // blocks
                if (command.isServerCommand) {
                    handleServerCommand(command);
                    continue;
                }

                // Find game by ID
                TicTacToe game = this.games.get(command.gameId);
                if (game != null) {
                    game.addCommandToQueue(command);
                    logger.info("Dispatched command {} to game {}", command.toString(), command.gameId);
                } else {
                    logger.warn("No active game with ID {} for command {}", command.gameId, command.toString());
                    // TODO: reply back
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Dispatcher interrupted");
        }
    }

    private void handleServerCommand(ParsedCommand command) {
        if (command.isValidCommand) {
            this.sendQueue.offer("ok");
        }

        if (command.command == TicTacToeServerCommand.CREATE_GAME) {
            String gameId = this.newGame((String) command.arguments.getFirst(), (String) command.arguments.get(1));
            this.sendQueue.offer("game created successfully|gameid " + gameId);
        } else if (command.command == TicTacToeServerCommand.START_GAME) {
            boolean success = this.runGame((String) command.arguments.getFirst());
            if (success) {this.sendQueue.offer("svr game is running successfully");}
            else {this.sendQueue.offer("svr running game failed");}
        } else if (command.command == TicTacToeServerCommand.END_GAME) {
            this.endGame((String) command.arguments.getFirst());
            this.sendQueue.offer("svr game ended successfully");
        } else if (command.command == TicTacToeServerCommand.LOGIN) {
            this.endGame((String) command.arguments.getFirst());
            this.sendQueue.offer("svr login successful");
        } else if (command.command == TicTacToeServerCommand.SUBSCRIBE) {
            this.endGame((String) command.arguments.getFirst());
            this.sendQueue.offer("svr added {} to the queue");
        }
    }

    public void forwardGameMessages(TicTacToe game) {
        dispatcherExecutor.submit(() -> {
            try {
                while (isRunning()) {
                    String msg = game.sendQueue.take(); // blocks until a message is added to the queue
                    logger.info("Games: {}, Adding: {} to the send queue", game.gameId, msg);
                    this.sendQueue.put(msg); // push to network layer
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public String newGame(String playerA, String playerB) {
        logger.info("Creating a new game: {} vs {}", playerA, playerB);
        String gameId = UUID.randomUUID().toString();
        TicTacToe game = new TicTacToe(playerA, playerB, gameId);
        this.games.put(gameId, game);
        forwardGameMessages(game);
        logger.info("Created new game: {}. {} vs {}", gameId, playerA, playerB);
        return gameId;
    }

    public boolean runGame(String gameId) {
        TicTacToe game = this.games.get(gameId);
        if (game != null) {
            game.run();
            logger.info("Running game: {}, players: {}", gameId, game.getPlayers());
            return true;
        } else {
            logger.warn("Tried to run unknown game {}", gameId);
            return false;
        }
    }

    public void endGame(String gameId) {
        TicTacToe game = this.games.remove(gameId);
        if (game != null) {
            logger.info("Ended game: {}", gameId);
            // TODO: gracefully stop game thread
        } else {
            logger.warn("Tried to end unknown game {}", gameId);
        }
    }
}