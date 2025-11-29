package org.toop.app.game;

import javafx.animation.SequentialTransition;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.ReversiCanvas;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.view.GameView;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;
import org.toop.game.reversi.Reversi;
import org.toop.game.reversi.ReversiAI;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ReversiGame {
	private final GameInformation information;

	private final int myTurn;
    private final Runnable onGameOver;
    private final BlockingQueue<Move> moveQueue;

	private final Reversi game;
	private final ReversiAI ai;

	private final GameView primary;
	private final ReversiCanvas canvas;

	private final AtomicBoolean isRunning;
	private final AtomicBoolean isPaused;

	public ReversiGame(GameInformation information, int myTurn, Runnable onForfeit, Runnable onExit, Consumer<String> onMessage, Runnable onGameOver) {
		this.information = information;

		this.myTurn = myTurn;
        this.onGameOver = onGameOver;
        moveQueue = new LinkedBlockingQueue<>();

		game = new Reversi();
		ai = new ReversiAI();

		isRunning = new AtomicBoolean(true);
		isPaused = new AtomicBoolean(false);

		if (onForfeit == null || onExit == null) {
			primary = new GameView(null, () -> {
				isRunning.set(false);
				WidgetContainer.getCurrentView().transitionPrevious();
			}, null);
		} else {
			primary = new GameView(onForfeit, () -> {
				isRunning.set(false);
				onExit.run();
			}, onMessage);
		}

		canvas = new ReversiCanvas(Color.BLACK,
			(App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,
			(cell) -> {
				if (onForfeit == null || onExit == null) {
					if (information.players[game.getCurrentTurn()].isHuman) {
						final char value = game.getCurrentTurn() == 0? 'B' : 'W';

						try {
							moveQueue.put(new Move(cell, value));
						} catch (InterruptedException _) {}
					}
				} else {
					if (information.players[0].isHuman) {
						final char value = myTurn == 0? 'B' : 'W';

						try {
							moveQueue.put(new Move(cell, value));
						} catch (InterruptedException _) {}
					}
				}
			},this::highlightCells);



		primary.add(Pos.CENTER, canvas.getCanvas());
		WidgetContainer.getCurrentView().transitionNext(primary);

		if (onForfeit == null || onExit == null) {
			new Thread(this::localGameThread).start();
			setGameLabels(information.players[0].isHuman);
		} else {
			new EventFlow()
				.listen(NetworkEvents.GameMoveResponse.class, this::onMoveResponse)
				.listen(NetworkEvents.YourTurnResponse.class, this::onYourTurnResponse);

			setGameLabels(myTurn == 0);
		}

		updateCanvas(false);
	}

	public ReversiGame(GameInformation information) {
		this(information, 0, null, null, null,null);
	}

	private void localGameThread() {
		while (isRunning.get()) {
			if (isPaused.get()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException _) {}

				continue;
			}

			final int currentTurn = game.getCurrentTurn();
			final String currentValue = currentTurn == 0? "BLACK" : "WHITE";
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
						Thread.sleep((long) (sleepTime * Math.random()));
					} catch (InterruptedException _) {}
				}
			}

			if (move == null) {
				continue;
			}

            canvas.setCurrentlyHighlightedMovesNull();
			final GameState state = game.play(move);
			updateCanvas(true);

			if (state != GameState.NORMAL) {
                if (state == GameState.TURN_SKIPPED){
                    continue;
                }
                int winningPLayerNumber = getPlayerNumberWithHighestScore();
				if (state == GameState.WIN && winningPLayerNumber > -1) {
					primary.gameOver(true, information.players[winningPLayerNumber].name);
				} else if (state == GameState.DRAW || winningPLayerNumber == -1) {
					primary.gameOver(false, "");
				}

				isRunning.set(false);
			}
		}
	}

    private int getPlayerNumberWithHighestScore(){
        Reversi.Score score = game.getScore();
        if (score.player1Score() > score.player2Score()) return 0;
        if (score.player1Score() < score.player2Score()) return 1;
        return -1;
    }

	private void onMoveResponse(NetworkEvents.GameMoveResponse response) {
		if (!isRunning.get()) {
			return;
		}

		char playerChar;

		if (response.player().equalsIgnoreCase(information.players[0].name)) {
			playerChar = myTurn == 0? 'B' : 'W';
		} else {
			playerChar = myTurn == 0? 'W' : 'B';
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
				primary.gameOver(false, "");
                game.play(move);
			}
		}

		updateCanvas(false);
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
			final Move move = ai.findBestMove(game, information.players[0].computerDifficulty);

			assert move != null;
			position = move.position();
		}

		new EventFlow().addPostEvent(new NetworkEvents.SendMove(response.clientId(), (short) position))
			.postEvent();
	}

	private void updateCanvas(boolean animate) {
		// Todo: this is very inefficient. still very fast but if the grid is bigger it might cause issues. improve.
		canvas.clearAll();

		for (int i = 0; i < game.getBoard().length; i++) {
			if (game.getBoard()[i] == 'B') {
				canvas.drawDot(Color.BLACK, i);
			} else if (game.getBoard()[i] == 'W') {
				canvas.drawDot(Color.WHITE, i);
			}
		}

		final Move[] flipped = game.getMostRecentlyFlippedPieces();

		final SequentialTransition animation = new SequentialTransition();
		isPaused.set(true);

		final Color fromColor = game.getCurrentPlayer() == 'W'? Color.WHITE : Color.BLACK;
		final Color toColor = game.getCurrentPlayer() == 'W'? Color.BLACK : Color.WHITE;

		if (animate && flipped != null) {
			for (final Move flip : flipped) {
				canvas.clear(flip.position());
				canvas.drawDot(fromColor, flip.position());
				animation.getChildren().addFirst(canvas.flipDot(fromColor, toColor, flip.position()));
			}
		}

		animation.setOnFinished(_ -> {
			isPaused.set(false);

            if (information.players[game.getCurrentTurn()].isHuman) {
                final Move[] legalMoves = game.getLegalMoves();

                for (final Move legalMove : legalMoves) {
                    canvas.drawLegalPosition(legalMove.position(), game.getCurrentPlayer());
                }
            }
		});

		animation.play();
	}

	private void setGameLabels(boolean isMe) {
		final int currentTurn = game.getCurrentTurn();
		final String currentValue = currentTurn == 0? "BLACK" : "WHITE";

		primary.nextPlayer(isMe,
			information.players[isMe? 0 : 1].name,
			currentValue,
			information.players[isMe? 1 : 0].name);
	}

    private void highlightCells(int cellEntered) {
        if (information.players[game.getCurrentTurn()].isHuman) {
            Move[] legalMoves = game.getLegalMoves();
            boolean isLegalMove = false;
            for (Move move : legalMoves) {
                if (move.position() == cellEntered){
                    isLegalMove = true;
                    break;
                }
            }

            if (cellEntered >= 0){
                Move[] moves = null;
                if (isLegalMove) {
                    moves = game.getFlipsForPotentialMove(
                            new Point(cellEntered%game.getColumnSize(),cellEntered/game.getRowSize()),
                            game.getCurrentPlayer());
                }
                canvas.drawHighlightDots(moves);
            }
        }
    }
}