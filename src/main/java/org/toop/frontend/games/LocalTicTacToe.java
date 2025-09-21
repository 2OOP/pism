package org.toop.frontend.games;

import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.frontend.UI.UIGameBoard;
import org.toop.game.tictactoe.GameBase;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.ai.MinMaxTicTacToe;

/**
 * A representation of a local tic-tac-toe game. Calls are made to a server for information about
 * current game state. MOST OF THIS CODE IS TRASH, THROW IT OUT OF THE WINDOW AFTER DEMO.
 */
public class LocalTicTacToe { // TODO: Implement runnable
    private static final Logger logger = LogManager.getLogger(LocalTicTacToe.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final BlockingQueue<String> receivedQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> moveQueuePlayerA = new LinkedBlockingQueue<>();
    private final BlockingQueue<Integer> moveQueuePlayerB = new LinkedBlockingQueue<>();

    private Object receivedMessageListener = null;

    private boolean isLocal;
    private String gameId;
    private String connectionId = null;
    private String serverId = null;

    private boolean isAiPlayer[] = new boolean[2];
    private MinMaxTicTacToe[] aiPlayers = new MinMaxTicTacToe[2];
    private TicTacToe ticTacToe;
    private UIGameBoard ui;

    /** Is either 0 or 1. */
    private int playersTurn = 0;

    /**
     * @return The current players turn.
     */
    public int getCurrentPlayersTurn() {
        return this.playersTurn;
    }

    //    LocalTicTacToe(String gameId, String connectionId, String serverId) {
    //        this.gameId = gameId;
    //        this.connectionId = connectionId;
    //        this.serverId = serverId;
    //        this.receivedMessageListener =
    // GlobalEventBus.subscribe(Events.ServerEvents.ReceivedMessage.class,
    // this::receiveMessageAction);
    //        GlobalEventBus.register(this.receivedMessageListener);
    //
    //
    //        this.executor.submit(this::gameThread);
    //    } TODO: If remote server

    /**
     * Starts a connection with a remote server.
     *
     * @param ip The IP of the server to connect to.
     * @param port The port of the server to connect to.
     */
    private LocalTicTacToe(String ip, String port) {
        this.receivedMessageListener =
                GlobalEventBus.subscribe(
                        Events.ServerEvents.ReceivedMessage.class, this::receiveMessageAction);
        GlobalEventBus.register(this.receivedMessageListener);
        this.connectionId = this.createConnection(ip, port);
        this.createGame(ip, port);
        this.isLocal = false;
        this.executor.submit(this::remoteGameThread);
    }

    private LocalTicTacToe(boolean[] aiFlags) {
        this.isAiPlayer = aiFlags; // store who is AI

        for (int i = 0; i < aiFlags.length && i < this.aiPlayers.length; i++) {
            if (aiFlags[i]) {
                this.aiPlayers[i] = new MinMaxTicTacToe(); // create AI for that player
            } else {
                this.aiPlayers[i] = null; // not an AI player
            }
        }

        this.isLocal = true;
        this.executor.submit(this::localGameThread);
    }

    public static LocalTicTacToe createLocal(boolean[] aiPlayers) {
        return new LocalTicTacToe(aiPlayers);
    }

    public static LocalTicTacToe createRemote(String ip, String port) {
        return new LocalTicTacToe(ip, port);
    }

    private String createServer(String port) {
        CompletableFuture<String> serverIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(
                new Events.ServerEvents.StartServerRequest(port, "tictactoe", serverIdFuture));
        try {
            return serverIdFuture.get();
        } catch (Exception e) {
            logger.error("Error getting server ID", e);
        }
        return null;
    }

    private String createConnection(String ip, String port) {
        CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();
        GlobalEventBus.post(
                new Events.ServerEvents.StartConnectionRequest(
                        ip,
                        port,
                        connectionIdFuture)); // TODO: what if server couldn't be started with port.
        try {
            return connectionIdFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error getting connection ID", e);
        }
        return null;
    }

    private void createGame(String nameA, String nameB) {
        nameA = nameA.trim().replace(" ", "-");
        nameB = nameB.trim().replace(" ", "-");
        this.sendCommand("create_game", nameA, nameB);
    }

    private void startGame() {
        if (this.gameId == null) {
            return;
        }
        this.sendCommand("start_game", this.gameId);
    }

    private void localGameThread() {
        boolean running = true;
        this.ticTacToe = new TicTacToe("X", "O");
        while (running) {
            try {
                GameBase.State state;
                if (!isAiPlayer[0]) {
                    state = this.ticTacToe.play(this.moveQueuePlayerA.take());
                } else {
                    int bestMove = aiPlayers[0].findBestMove(this.ticTacToe);
                    state = this.ticTacToe.play(bestMove);
                    if (state != GameBase.State.INVALID) {
                        ui.setCell(bestMove, "X");
                    }
                }
                if (state == GameBase.State.WIN || state == GameBase.State.DRAW) {
                    ui.setState(state, "X");
                    running = false;
                }
                this.setNextPlayersTurn();
                if (!isAiPlayer[1]) {
                    state = this.ticTacToe.play(this.moveQueuePlayerB.take());
                } else {
                    int bestMove = aiPlayers[1].findBestMove(this.ticTacToe);
                    state = this.ticTacToe.play(bestMove);
                    if (state != GameBase.State.INVALID) {
                        ui.setCell(bestMove, "O");
                    }
                }
                if (state == GameBase.State.WIN || state == GameBase.State.DRAW) {
                    ui.setState(state, "O");
                    running = false;
                }
                this.setNextPlayersTurn();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void remoteGameThread() {
        // TODO: If server start this.
    }

    public void setNextPlayersTurn() {
        if (this.playersTurn == 0) {
            this.playersTurn += 1;
        } else {
            this.playersTurn -= 1;
        }
    }

    public char[] getCurrentBoard() {
        return ticTacToe.getGrid();
    }

    /** End the current game. */
    public void endGame() {
        sendCommand("gameid", "end_game"); // TODO: Command is a bit wrong.
    }

    /**
     * @param moveIndex The index of the move to make.
     */
    public void move(int moveIndex) {
        this.executor.submit(
                () -> {
                    try {
                        if (this.playersTurn == 0 && !isAiPlayer[0]) {
                            this.moveQueuePlayerA.put(moveIndex);
                            logger.info(
                                    "Adding player's {}, move: {} to queue A",
                                    this.playersTurn,
                                    moveIndex);
                        } else if (this.playersTurn == 1 && !isAiPlayer[1]) {
                            this.moveQueuePlayerB.put(moveIndex);
                            logger.info(
                                    "Adding player's {}, move: {} to queue B",
                                    this.playersTurn,
                                    moveIndex);
                        }
                    } catch (InterruptedException e) {
                        logger.error(
                                "Could not add player: {}'s, move {}",
                                this.playersTurn,
                                moveIndex); // TODO: Error handling instead of crash.
                    }
                });
    }

    private void endTheGame() {
        this.sendCommand("end_game", this.gameId);
        this.endListeners();
    }

    private void receiveMessageAction(Events.ServerEvents.ReceivedMessage receivedMessage) {
        if (!receivedMessage.ConnectionUuid().equals(this.connectionId)) {
            return;
        }

        try {
            logger.info(
                    "Received message from {}: {}", this.connectionId, receivedMessage.message());
            this.receivedQueue.put(receivedMessage.message());
        } catch (InterruptedException e) {
            logger.error("Error waiting for received Message", e);
        }
    }

    private void sendCommand(String... args) {
        GlobalEventBus.post(new Events.ServerEvents.SendCommand(this.connectionId, args));
    }

    private void endListeners() {
        GlobalEventBus.unregister(this.receivedMessageListener);
    }

    public void setUIReference(UIGameBoard uiGameBoard) {
        this.ui = uiGameBoard;
    }
}
