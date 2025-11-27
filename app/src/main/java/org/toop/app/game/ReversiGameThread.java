package org.toop.app.game;

import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.ReversiCanvas;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;
import org.toop.game.reversi.Reversi;
import org.toop.game.reversi.ReversiAI;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class ReversiGameThread extends BaseGameThread<Reversi, ReversiAI, ReversiCanvas> {
	public ReversiGameThread(GameInformation info, int myTurn,
							 Runnable onForfeit, Runnable onExit, Consumer<String> onMessage, Runnable onGameOver) {
		super(info, myTurn, onForfeit, onExit, onMessage, onGameOver,
			Reversi::new,
			ReversiAI::new,
			clickHandler -> new ReversiCanvas(
				Color.BLACK,
				(App.getHeight() / 4) * 3,
				(App.getHeight() / 4) * 3,
				clickHandler
			)
		);

		canvas.setOnCellEntered(this::highlightCells);
	}

	public ReversiGameThread(GameInformation info) {
		this(info, 0, null, null, null, null);
	}

	@Override
	protected void addCanvasToPrimary() {
		primary.add(Pos.CENTER, canvas.getCanvas());
	}

	@Override
	protected int getCurrentTurn() {
		return game.getCurrentTurn();
	}

	@Override
	protected char getSymbolForTurn(int turn) {
		return turn == 0 ? 'B' : 'W';
	}

	@Override
	protected String getNameForTurn(int turn) {
		return turn == 0 ? "BLACK" : "WHITE";
	}

	private void drawMove(Move move) {
		if (move.value() == 'B') canvas.drawDot(Color.BLACK, move.position());
		else canvas.drawDot(Color.WHITE, move.position());
	}

	@Override
	protected void onMoveResponse(NetworkEvents.GameMoveResponse response) {
		if (!isRunning.get()) return;

		char playerChar =
			response.player().equalsIgnoreCase(information.players[0].name)
				? (myTurn == 0 ? 'B' : 'W')
				: (myTurn == 0 ? 'W' : 'B');

		final Move move = new Move(Integer.parseInt(response.move()), playerChar);
		final GameState state = game.play(move);

		updateCanvas(true);

		if (state != GameState.NORMAL) {
			if (state == GameState.WIN) {
				boolean p0 = response.player().equalsIgnoreCase(information.players[0].name);
				primary.gameOver(p0, information.players[p0 ? 0 : 1].name);
				gameOver();
			} else if (state == GameState.DRAW) {
				primary.gameOver(false, "");
				gameOver();
			}
		}

		setGameLabels(game.getCurrentTurn() == myTurn);
	}

	@Override
	protected void onYourTurnResponse(NetworkEvents.YourTurnResponse response) {
		if (!isRunning.get()) return;

		moveQueue.clear();
		int position = -1;

		if (information.players[0].isHuman) {
			try {
				position = moveQueue.take().position();
			} catch (InterruptedException _) {
			}
		} else {
			final Move move = ai.findBestMove(game, information.players[0].computerDifficulty);
			assert move != null;
			position = move.position();
		}

		new EventFlow()
			.addPostEvent(new NetworkEvents.SendMove(response.clientId(), (short) position))
			.postEvent();
	}

	@Override
	protected void localGameThread() {
		while (isRunning.get()) {
			if (isPaused.get()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException _) {}

				continue;
			}

			final int currentTurn = game.getCurrentTurn();
			setGameLabels(currentTurn == myTurn);

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
				} catch (InterruptedException _) {
				}

			} else {
				long start = System.currentTimeMillis();
				move = ai.findBestMove(game, information.players[currentTurn].computerDifficulty);

				if (information.players[currentTurn].computerThinkTime > 0) {
					long elapsed = System.currentTimeMillis() - start;
					long sleep = information.players[currentTurn].computerThinkTime * 1000L - elapsed;

					try {
						Thread.sleep((long) (sleep * Math.random()));
					} catch (InterruptedException _) {
					}
				}
			}

			if (move == null) continue;

			GameState state = game.play(move);
			updateCanvas(true);

			if (state != GameState.NORMAL) {
				if (state == GameState.WIN) {
					primary.gameOver(information.players[currentTurn].isHuman,
						information.players[currentTurn].name);
				} else if (state == GameState.DRAW) {
					primary.gameOver(false, "");
				}

				isRunning.set(false);
			}
		}
	}

	private void updateCanvas(boolean animate) {
		canvas.clearAll();

		for (int i = 0; i < game.getBoard().length; i++) {
			char c = game.getBoard()[i];
			if (c == 'B') canvas.drawDot(Color.BLACK, i);
			else if (c == 'W') canvas.drawDot(Color.WHITE, i);
		}

		final Move[] flipped = game.getMostRecentlyFlippedPieces();

		final SequentialTransition anim = new SequentialTransition();
		isPaused.set(true);

		final Color from = game.getCurrentPlayer() == 'W' ? Color.WHITE : Color.BLACK;
		final Color to = game.getCurrentPlayer() == 'W' ? Color.BLACK : Color.WHITE;

		if (animate && flipped != null) {
			for (final Move flip : flipped) {
				canvas.clear(flip.position());
				canvas.drawDot(from, flip.position());
				anim.getChildren().addFirst(canvas.flipDot(from, to, flip.position()));
			}
		}

		anim.setOnFinished(_ -> {
			isPaused.set(false);

			for (final Move m : game.getLegalMoves()) {
				canvas.drawLegalPosition(m.position(), game.getCurrentPlayer());
			}
		});

		anim.play();
	}

	private void highlightCells(int cell) {
		Move[] legal = game.getLegalMoves();
		boolean isLegal = false;

		for (Move m : legal) {
			if (m.position() == cell) {
				isLegal = true;
				break;
			}
		}

		if (cell >= 0) {
			Move[] flips = null;

			if (isLegal) {
				flips = game.getFlipsForPotentialMove(
					new Point(cell % game.getColumnSize(), cell / game.getRowSize()),
					game.getCurrentPlayer()
				);
			}

			canvas.drawHighlightDots(flips);
		}
	}
}