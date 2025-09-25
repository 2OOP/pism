package org.toop.tictactoe;

import java.util.concurrent.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.Game;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.TicTacToeAI;
import org.toop.tictactoe.gui.UIGameBoard;
import org.toop.framework.networking.NetworkingGameClientHandler;

import java.util.function.Supplier;

import static java.lang.Thread.sleep;

/**
 * A representation of a local tic-tac-toe game. Calls are made to a server for information about
 * current game state. MOST OF THIS CODE IS TRASH, THROW IT OUT OF THE WINDOW AFTER DEMO.
 */
// Todo: refactor
public class LocalTicTacToe { // TODO: Implement runnable
    private static final Logger logger = LogManager.getLogger(LocalTicTacToe.class);

    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final BlockingQueue<String> receivedQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<Game.Move> moveQueuePlayerA = new LinkedBlockingQueue<>();
    private final BlockingQueue<Game.Move> moveQueuePlayerB = new LinkedBlockingQueue<>();

    private Object receivedMessageListener = null;

    private boolean isLocal;
    private String gameId;
    private String connectionId = null;
    private String serverId = null;

    private boolean[] isAiPlayer = new boolean[2];
    private TicTacToeAI ai = new TicTacToeAI();
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
    private LocalTicTacToe(String ip, int port) {
//        this.receivedMessageListener =
//                GlobalEventBus.subscribe(this::receiveMessageAction);
//        GlobalEventBus.subscribe(this.receivedMessageListener);
        this.connectionId = this.createConnection(ip, port);
        this.createGame("X", "O");
        this.isLocal = false;
        //this.executor.submit(this::remoteGameThread);
    }

    private LocalTicTacToe(boolean[] aiFlags) {
        this.isAiPlayer = aiFlags; // store who is AI
        this.isLocal = true;
        //this.executor.submit(this::localGameThread);
    }
    public void startThreads(){
        if (isLocal) {
            this.executor.submit(this::localGameThread);
        }else {
            this.executor.submit(this::remoteGameThread);
        }
    }

    public static LocalTicTacToe createLocal(boolean[] aiPlayers) {
        return new LocalTicTacToe(aiPlayers);
    }

    public static LocalTicTacToe createRemote(String ip, int port) {
        return new LocalTicTacToe(ip, port);
    }

    private String createConnection(String ip, int port) {
        CompletableFuture<String> connectionIdFuture = new CompletableFuture<>();
        new EventFlow().addPostEvent(NetworkEvents.StartClientRequest.class,
                (Supplier<NetworkingGameClientHandler>) NetworkingGameClientHandler::new,
                ip, port, connectionIdFuture).asyncPostEvent(); // TODO: what if server couldn't be started with port.
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
                Game.State state;
                if (!isAiPlayer[0]) {
                    state = this.ticTacToe.play(this.moveQueuePlayerA.take());
                } else {
                    Game.Move bestMove = ai.findBestMove(this.ticTacToe, 9);
	                assert bestMove != null;

	                state = this.ticTacToe.play(bestMove);
	                ui.setCell(bestMove.position(), "X");
                }
                if (state == Game.State.WIN || state == Game.State.DRAW) {
                    ui.setState(state, "X");
                    running = false;
                }
                this.setNextPlayersTurn();
                if (!isAiPlayer[1]) {
                    state = this.ticTacToe.play(this.moveQueuePlayerB.take());
                } else {
                    Game.Move bestMove = ai.findBestMove(this.ticTacToe, 9);
	                assert bestMove != null;
	                state = this.ticTacToe.play(bestMove);
	                ui.setCell(bestMove.position(), "O");
                }
                if (state == Game.State.WIN || state == Game.State.DRAW) {
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
        //return ticTacToe.getGrid();
	    return new char[2];
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
                            this.moveQueuePlayerA.put(new Game.Move(moveIndex, 'X'));
                            logger.info(
                                    "Adding player's {}, move: {} to queue A",
                                    this.playersTurn,
                                    moveIndex);
                        } else if (this.playersTurn == 1 && !isAiPlayer[1]) {
                            this.moveQueuePlayerB.put(new Game.Move(moveIndex, 'O'));
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
//        this.endListeners();
    }

    private void receiveMessageAction(NetworkEvents.ReceivedMessage receivedMessage) {
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
        new EventFlow().addPostEvent(NetworkEvents.SendCommand.class, this.connectionId, args).asyncPostEvent();
    }

//    private void endListeners() {
//        GlobalEventBus.unregister(this.receivedMessageListener);
//    } TODO

    public void setUIReference(UIGameBoard uiGameBoard) {
        this.ui = uiGameBoard;
    }
}
