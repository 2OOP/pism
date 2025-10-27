package org.toop.app.game;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.Connect4Canvas;
import org.toop.app.view.ViewStack;
import org.toop.app.view.views.GameView;
import org.toop.app.view.views.LocalMultiplayerView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.Connect4.Connect4;
import org.toop.game.Connect4.Connect4AI;
import org.toop.game.Game;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Connect4Game {
    private final GameInformation information;

    private final int myTurn;
    private Runnable onGameOver;
    private final BlockingQueue<Game.Move> moveQueue;

    private final Connect4 game;
    private final Connect4AI ai;
    private final int columnSize = 7;
    private final int rowSize = 6;

    private final GameView view;
    private final Connect4Canvas canvas;

    private final AtomicBoolean isRunning;

    public Connect4Game(GameInformation information, int myTurn, Runnable onForfeit, Runnable onExit, Consumer<String> onMessage, Runnable onGameOver) {
        this.information = information;
        this.myTurn = myTurn;
        this.onGameOver = onGameOver;
        moveQueue = new LinkedBlockingQueue<Game.Move>();


        game = new Connect4();
        ai = new Connect4AI();

        isRunning = new AtomicBoolean(true);

        if (onForfeit == null || onExit == null) {
            view = new GameView(null, () -> {
                isRunning.set(false);
                ViewStack.push(new LocalMultiplayerView(information));
            }, null);
        } else {
            view = new GameView(onForfeit, () -> {
                isRunning.set(false);
                onExit.run();
            }, onMessage);
        }

        canvas = new Connect4Canvas(Color.GRAY,
                (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,
                (cell) -> {
                    if (onForfeit == null || onExit == null) {
                        if (information.players[game.getCurrentTurn()].isHuman) {
                            final char value = game.getCurrentTurn() == 0? 'X' : 'O';

                            try {
                                moveQueue.put(new Game.Move(cell%columnSize, value));
                            } catch (InterruptedException _) {}
                        }
                    } else {
                        if (information.players[0].isHuman) {
                            final char value = myTurn == 0? 'X' : 'O';

                            try {
                                moveQueue.put(new Game.Move(cell%columnSize, value));
                            } catch (InterruptedException _) {}
                        }
                    }
                });

        view.add(Pos.CENTER, canvas.getCanvas());
        ViewStack.push(view);

        if (onForfeit == null || onExit == null) {
            new Thread(this::localGameThread).start();
        } else {
            new EventFlow()
                    .listen(NetworkEvents.GameMoveResponse.class, this::onMoveResponse)
                    .listen(NetworkEvents.YourTurnResponse.class, this::onYourTurnResponse)
                    .listen(NetworkEvents.ReceivedMessage.class, this::onReceivedMessage);

            setGameLabels(myTurn == 0);
        }
        updateCanvas();
    }

    public Connect4Game(GameInformation information) {
        this(information, 0, null, null, null, null);
    }
    private void localGameThread() {
        while (isRunning.get()) {
			final int currentTurn = game.getCurrentTurn();
			setGameLabels(information.players[currentTurn].isHuman);

            Game.Move move = null;

            if (information.players[currentTurn].isHuman) {
                try {
                    final Game.Move wants = moveQueue.take();
                    final Game.Move[] legalMoves = game.getLegalMoves();

                    for (final Game.Move legalMove : legalMoves) {
                        if (legalMove.position() == wants.position() &&
                                legalMove.value() == wants.value()) {
                            move = wants;
                            break;
                        }
                    }
                } catch (InterruptedException _) {}
            } else {
                final long start = System.currentTimeMillis();

                move = ai.findBestMove(game, information.players[currentTurn].computerDifficulty);

                if (information.players[currentTurn].computerThinkTime > 0) {
                    final long elapsedTime = System.currentTimeMillis() - start;
                    final long sleepTime = Math.abs(information.players[currentTurn].computerThinkTime * 1000L - elapsedTime);

                    try {
                        Thread.sleep((long)(sleepTime * Math.random()));
                    } catch (InterruptedException _) {}
                }
            }

            if (move == null) {
                continue;
            }

            final Game.State state = game.play(move);
            updateCanvas();
/*
            if (move.value() == 'X') {
                canvas.drawX(Color.INDIANRED, move.position());
            } else if (move.value() == 'O') {
                canvas.drawO(Color.ROYALBLUE, move.position());
            }
*/
            if (state != Game.State.NORMAL) {
                if (state == Game.State.WIN) {
                    view.gameOver(true, information.players[currentTurn].name);
                } else if (state == Game.State.DRAW) {
                    view.gameOver(false, "");
                }

                isRunning.set(false);
            }
        }
    }

    private void onMoveResponse(NetworkEvents.GameMoveResponse response) {
        if (!isRunning.get()) {
            return;
        }

        char playerChar;

        if (response.player().equalsIgnoreCase(information.players[0].name)) {
            playerChar = myTurn == 0? 'X' : 'O';
        } else {
            playerChar = myTurn == 0? 'O' : 'X';
        }

        final Game.Move move = new Game.Move(Integer.parseInt(response.move()), playerChar);
        final Game.State state = game.play(move);

        if (state != Game.State.NORMAL) {
            if (state == Game.State.WIN) {
                if (response.player().equalsIgnoreCase(information.players[0].name)) {
                    view.gameOver(true, information.players[0].name);
                    gameOver();
                } else {
                    view.gameOver(false, information.players[1].name);
                    gameOver();
                }
            } else if (state == Game.State.DRAW) {
                view.gameOver(false, "");
                gameOver();
            }
        }

        if (move.value() == 'X') {
            canvas.drawDot(Color.INDIANRED, move.position());
        } else if (move.value() == 'O') {
            canvas.drawDot(Color.ROYALBLUE, move.position());
        }

        updateCanvas();
        setGameLabels(game.getCurrentTurn() == myTurn);
    }

    private void gameOver() {
        if (onGameOver == null){
            return;
        }
        isRunning.set(false);
        onGameOver.run();
    }

    private void onYourTurnResponse(NetworkEvents.YourTurnResponse response) {

        if (!isRunning.get()) {
            return;
        }

        moveQueue.clear();

        int position = -1;

        if (information.players[0].isHuman) {
            try {
                position = moveQueue.take().position();
            } catch (InterruptedException _) {}
        } else {
            final Game.Move move = ai.findBestMove(game, information.players[0].computerDifficulty);

            assert move != null;
            position = move.position();
        }

        new EventFlow().addPostEvent(new NetworkEvents.SendMove(response.clientId(), (short)position))
                .postEvent();
    }

    private void onReceivedMessage(NetworkEvents.ReceivedMessage msg) {
        if (!isRunning.get()) {
            return;
        }

        view.updateChat(msg.message());
    }

    private void updateCanvas() {
        canvas.clearAll();

        for (int i = 0; i < game.board.length; i++) {
            if (game.board[i] == 'X') {
                canvas.drawDot(Color.RED, i);
            } else if (game.board[i] == 'O') {
                canvas.drawDot(Color.BLUE, i);
            }
        }
    }

    private void setGameLabels(boolean isMe) {
        final int currentTurn = game.getCurrentTurn();
        final String currentValue = currentTurn == 0? "RED" : "BLUE";

        view.nextPlayer(isMe,
                information.players[isMe? 0 : 1].name,
                currentValue,
                information.players[isMe? 1 : 0].name);
    }
}
