package org.toop.app.game;

import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.TicTacToeAI;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public final class TicTacToeGameThread extends BaseGameThread<TicTacToe, TicTacToeAI, TicTacToeCanvas> {
	public TicTacToeGameThread(GameInformation info, int myTurn, Runnable onForfeit, Runnable onExit, Consumer<String> onMessage, Runnable onGameOver) {
		super(info, myTurn, onForfeit, onExit, onMessage, onGameOver,
			TicTacToe::new,
			TicTacToeAI::new,
			clickHandler -> new TicTacToeCanvas(Color.GRAY, (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3, clickHandler)
		);
	}

	public TicTacToeGameThread(GameInformation info) {
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
		return turn == 0 ? 'X' : 'O';
	}

	@Override
	protected String getNameForTurn(int turn) {
		return turn == 0 ? "X" : "O";
	}

	private void drawMove(Move move) {
		if (move.value() == 'X') canvas.drawPlayer('X', Color.RED, move.position());
		else canvas.drawPlayer('O', Color.BLUE, move.position());
	}

	@Override
	protected void onMoveResponse(NetworkEvents.GameMoveResponse response) {
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
				if (game.getLegalMoves().length == 0) {
					primary.gameOver(false, "");
					gameOver();
				}
			}
		}

		drawMove(move);
		setGameLabels(game.getCurrentTurn() == myTurn);
	}

	@Override
	protected void onYourTurnResponse(NetworkEvents.YourTurnResponse response) {
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
			if (information.players[1].name.equalsIgnoreCase("pism")) {
				move = ai.findWorstMove(game,9);
			}else{
				move = ai.findBestMove(game, information.players[0].computerDifficulty);
			}

			assert move != null;
			position = move.position();
		}

		new EventFlow().addPostEvent(new NetworkEvents.SendMove(response.clientId(), (short)position))
			.postEvent();
	}

	@Override
	protected void localGameThread() {
		while (isRunning.get()) {
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
			drawMove(move);

			if (state != GameState.NORMAL) {
				if (state == GameState.WIN) {
					primary.gameOver(information.players[currentTurn].isHuman, information.players[currentTurn].name);
				} else if (state == GameState.DRAW) {
					primary.gameOver(false, "");
				}

				isRunning.set(false);
			}
		}
	}
}