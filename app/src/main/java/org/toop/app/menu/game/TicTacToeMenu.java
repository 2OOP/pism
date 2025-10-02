package org.toop.app.menu.game;

import javafx.scene.paint.Color;
import org.toop.game.Game;
import org.toop.game.Player;
import org.toop.game.tictactoe.TicTacToe;
import org.toop.game.tictactoe.TicTacToeAI;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public final class TicTacToeMenu extends GameMenu {
	private final TicTacToe game;
	private final TicTacToeAI ai;

	private final ExecutorService executor = Executors.newFixedThreadPool(1);
	private final BlockingQueue<Game.Move> moveQueue = new LinkedBlockingQueue<>();

	public TicTacToeMenu(TicTacToe game) {
		super(3, 3, 10);

		graphics.setFill(Color.CYAN);

		for (int x = 1; x < rows; x++) {
			graphics.fillRect(cells[x].x - gapSize, 0, gapSize, size);
		}

		for (int y = 1; y < columns; y++) {
			graphics.fillRect(0, cells[y * rows].y - gapSize, size, gapSize);
		}

		this.game = game;
		ai = new TicTacToeAI();

		canvas.setOnMouseClicked(event -> {
			for (int i = 0; i < cells.length; i++) {
				if (cells[i].check((float) event.getX(), (float) event.getY())) {
					final Game.Move move = new Game.Move(i, game.getCurrentPlayer().values()[0]);
					play(move);
				}
			}
		});

		this.executor.submit(this::gameThread);
	}

	private void play(Game.Move move) {
		final Game.Move[] legalMoves = game.getLegalMoves();

		boolean isLegal = false;

		for (final Game.Move legalMove : legalMoves) {
			if (legalMove.position() == move.position() && legalMove.value() == move.value()) {
				isLegal = true;
				break;
			}
		}

		if (!isLegal) {
			return;
		}

		try { moveQueue.put(move); }
		catch (InterruptedException _) {}
	}

	private void placeX(int cell) {
		graphics.setStroke(Color.ORANGERED);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x + gapSize;
		final float y = cells[cell].y + gapSize;

		final float width = cells[cell].width - gapSize * 2;
		final float height = cells[cell].height - gapSize * 2;

		graphics.strokeLine(x, y, x + width, y + height);
		graphics.strokeLine(x + width, y, x, y + height);
	}

	private void placeO(int cell) {
		graphics.setStroke(Color.DEEPSKYBLUE);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x + gapSize;
		final float y = cells[cell].y + gapSize;

		final float width = cells[cell].width - gapSize * 2;
		final float height = cells[cell].height - gapSize * 2;

		graphics.strokeOval(x, y, width, height);
	}

	private void gameThread() {
		boolean running = true;

		while(running) {
			final Player currentPlayer = game.getCurrentPlayer();

			try {
				Game.Move move;

				if (!currentPlayer.isAI()) {
					try { move = moveQueue.take(); }
					catch (InterruptedException _) { return; }
				} else {
					move = ai.findBestMove(game, 9);
				}

				assert move != null;
				final Game.State state = game.play(move);

				if (move.value() == 'X') {
					placeX(move.position());
				} else {
					placeO(move.position());
				}

				switch (state) {
					case NORMAL: break;

					case DRAW:
					case LOSE:
					case WIN:
						running = false;
						break;
				}
			} catch (RuntimeException e) {
				return;
			}
		}

		executor.close();
	}
}