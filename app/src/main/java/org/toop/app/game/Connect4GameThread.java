package org.toop.app.game;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.canvas.Connect4Canvas;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.Connect4.Connect4;
import org.toop.game.Connect4.Connect4AI;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;

import java.util.function.Consumer;

public final class Connect4GameThread extends BaseGameThread<Connect4, Connect4AI, Connect4Canvas> {
	private static final int COLS = 7;

	public Connect4GameThread(
		GameInformation info,
		int myTurn,
		Runnable onForfeit,
		Runnable onExit,
		Consumer<String> onMessage,
		Runnable onGameOver
	) {
		super(
			info,
			myTurn,
			onForfeit,
			onExit,
			onMessage,
			onGameOver,
			Connect4::new,
			Connect4AI::new,
			clickHandler -> new Connect4Canvas(
				Color.GRAY,
				(App.getHeight() / 4) * 3,
				(App.getHeight() / 4) * 3,
				cell -> clickHandler.accept(cell % COLS)
			)
		);
	}

	public Connect4GameThread(GameInformation info) {
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
		return turn == 0 ? "RED" : "BLUE";
	}

	private void drawMove(Move move) {
		if (move.value() == 'X')
			canvas.drawDot(Color.RED, move.position());
		else
			canvas.drawDot(Color.BLUE, move.position());
	}

	@Override
	protected void onMoveResponse(NetworkEvents.GameMoveResponse response) {
		if (!isRunning.get()) return;

		char symbol =
			response.player().equalsIgnoreCase(information.players[0].name)
				? (myTurn == 0 ? 'X' : 'O')
				: (myTurn == 0 ? 'O' : 'X');

		final Move move = new Move(Integer.parseInt(response.move()), symbol);
		final GameState state = game.play(move);

		drawMove(move);
		updateCanvas();

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
		int col = -1;

		if (information.players[0].isHuman) {
			try {
				col = moveQueue.take().position();
			} catch (InterruptedException _) {
			}
		} else {
			final Move move = ai.findBestMove(game, information.players[0].computerDifficulty);
			assert move != null;
			col = move.position();
		}

		new EventFlow()
			.addPostEvent(new NetworkEvents.SendMove(response.clientId(), (short) col))
			.postEvent();
	}

	@Override
	protected void localGameThread() {
		while (isRunning.get()) {
			final int current = game.getCurrentTurn();
			setGameLabels(current == myTurn);

			Move move = null;

			if (information.players[current].isHuman) {
				try {
					final Move wants = moveQueue.take();
					for (final Move legal : game.getLegalMoves()) {
						if (legal.position() == wants.position() &&
								legal.value() == wants.value()) {
							move = wants;
							break;
						}
					}
				} catch (InterruptedException _) {
				}
			} else {
				final long start = System.currentTimeMillis();
				move = ai.findBestMove(game, information.players[current].computerDifficulty);

				if (information.players[current].computerThinkTime > 0) {
					long elapsed = System.currentTimeMillis() - start;
					long sleep = information.players[current].computerThinkTime * 1000L - elapsed;
					try {
						Thread.sleep((long) (sleep * Math.random()));
					} catch (InterruptedException _) {
					}
				}
			}

			if (move == null) continue;

			GameState state = game.play(move);
			drawMove(move);
			updateCanvas();

			if (state != GameState.NORMAL) {
				if (state == GameState.WIN)
					primary.gameOver(information.players[current].isHuman, information.players[current].name);
				else if (state == GameState.DRAW)
					primary.gameOver(false, "");
				isRunning.set(false);
			}
		}
	}

	private void updateCanvas() {
		canvas.clearAll();

		for (int i = 0; i < game.getBoard().length; i++) {
			char c = game.getBoard()[i];
			if (c == 'X') canvas.drawDot(Color.RED, i);
			else if (c == 'O') canvas.drawDot(Color.BLUE, i);
		}
	}
}