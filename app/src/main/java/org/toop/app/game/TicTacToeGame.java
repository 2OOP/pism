package org.toop.app.game;

import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.framework.gameFramework.GameState;
import org.toop.game.records.Move;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.TicTacToeAI;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class TicTacToeGame {
	private final GameInformation information;

	private final int myTurn;
    private final Runnable onGameOver;
    private final BlockingQueue<Move> moveQueue;

	private final TicTacToe game;
	private final TicTacToeAI ai;

	private final GameView primary;
	private final TicTacToeCanvas canvas;

	private final AtomicBoolean isRunning;

	public TicTacToeGame(GameInformation information, int myTurn, Runnable onForfeit, Runnable onExit, Consumer<String> onMessage, Runnable onGameOver) {
		this.information = information;

		this.myTurn = myTurn;
        this.onGameOver = onGameOver;
        moveQueue = new LinkedBlockingQueue<Move>();

		game = new TicTacToe();
		ai = new TicTacToeAI();

		isRunning = new AtomicBoolean(true);

		if (onForfeit == null || onExit == null) {
			primary = new GameView(null, () -> {
				isRunning.set(false);
				WidgetContainer.getCurrentView().transitionPrevious();
			}, null, "TicTacToe");
		} else {
			primary = new GameView(onForfeit, () -> {
				isRunning.set(false);
				onExit.run();
			}, onMessage, "TicTacToe");
		}

		canvas = new TicTacToeCanvas(Color.GRAY,
			(App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,
			(cell) -> {
				if (onForfeit == null || onExit == null) {
					if (information.players[game.getCurrentTurn()].isHuman) {
						final char value = game.getCurrentTurn() == 0? 'X' : 'O';

						try {
							moveQueue.put(new Move(cell, value));
						} catch (InterruptedException _) {}
					}
				} else {
					if (information.players[0].isHuman) {
						final char value = myTurn == 0? 'X' : 'O';

						try {
							moveQueue.put(new Move(cell, value));
						} catch (InterruptedException _) {}
					}
				}
			});

		primary.add(Pos.CENTER, canvas.getCanvas());
		WidgetContainer.getCurrentView().transitionNext(primary);

		if (onForfeit == null || onExit == null) {
			new Thread(this::localGameThread).start();
		} else {
			new EventFlow()
				.listen(NetworkEvents.GameMoveResponse.class, this::onMoveResponse)
				.listen(NetworkEvents.YourTurnResponse.class, this::onYourTurnResponse);

			setGameLabels(myTurn == 0);
		}
	}

	public TicTacToeGame(GameInformation information) {
		this(information, 0, null, null, null, null);
	}

	private void localGameThread() {
		while (isRunning.get()) {
			final int currentTurn = game.getCurrentTurn();
			final String currentValue = currentTurn == 0? "X" : "O";
			final int nextTurn = (currentTurn + 1) % information.type.getPlayerCount();

			primary.nextPlayer(information.players[currentTurn].isHuman,
				information.players[currentTurn].name,
				currentValue,
				information.players[nextTurn].name);

			Move move = null;

			if (information.players[currentTurn].isHuman) {
				try {
					final Move wants = moveQueue.take();
					final Move[] legalMoves = game.getLegalMoves();

					for (final Move legalMove : legalMoves) {
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
					final long sleepTime = information.players[currentTurn].computerThinkTime * 1000L - elapsedTime;

					try {
						Thread.sleep((long)(sleepTime * Math.random()));
					} catch (InterruptedException _) {}
				}
			}

			if (move == null) {
				continue;
			}

			final GameState state = game.play(move);

			if (move.value() == 'X') {
				//canvas.drawPlayer('X', Color.INDIANRED, move.position());
			} else if (move.value() == 'O') {
				//canvas.drawPlayer('O', Color.ROYALBLUE, move.position());
			}

			if (state != GameState.NORMAL) {
				if (state == GameState.WIN) {
					primary.gameOver(true, information.players[currentTurn].name);
				} else if (state == GameState.DRAW) {
					primary.gameOver(false, "");
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

	    final Move move = new Move(Integer.parseInt(response.move()), playerChar);
	    final GameState state = game.play(move);

	    if (state != GameState.NORMAL) {
		    if (state == GameState.WIN) {
			    if (response.player().equalsIgnoreCase(information.players[0].name)) {
					primary.gameOver(true, information.players[0].name);
                    gameOver();
			    } else {
				    primary.gameOver(false, information.players[1].name);
                    gameOver();
			    }
		    } else if (state == GameState.DRAW) {
                if(game.getLegalMoves().length == 0) { //only return draw in online multiplayer if the game is actually over.
                    primary.gameOver(false, "");
                    gameOver();
                }
		    }
	    }

	    if (move.value() == 'X') {
		    //canvas.drawPlayer('X', Color.RED, move.position());
	    } else if (move.value() == 'O') {
		    //canvas.drawPlayer('O', Color.BLUE, move.position());
	    }

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
             final Move move;
             move = ai.findBestMove(game, information.players[0].computerDifficulty);
			assert move != null;
 			position = move.position();
 		}

 		new EventFlow().addPostEvent(new NetworkEvents.SendMove(response.clientId(), (short)position))
 				.postEvent();
    }

	private void setGameLabels(boolean isMe) {
		final int currentTurn = game.getCurrentTurn();
		final String currentValue = currentTurn == 0? "X" : "O";

		primary.nextPlayer(isMe,
			information.players[isMe? 0 : 1].name,
			currentValue,
			information.players[isMe? 1 : 0].name);
	}
}