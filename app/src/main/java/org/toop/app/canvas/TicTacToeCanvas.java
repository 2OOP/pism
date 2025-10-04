package org.toop.app.canvas;

import org.toop.game.Game;
import org.toop.game.tictactoe.TicTacToe;

import javafx.scene.paint.Color;

public class TicTacToeCanvas extends GameCanvas {
	private final TicTacToe game;

	public TicTacToeCanvas(int width, int height) {
		super(width, height, 3, 3, 10);
		game = new TicTacToe();

		graphics.setFill(Color.CYAN);

		for (int x = 1; x < rows; x++) {
			graphics.fillRect(cells[x].x() - gapSize, 0, gapSize, height);
		}

		for (int y = 1; y < columns; y++) {
			graphics.fillRect(0, cells[y * rows].y() - gapSize, width, gapSize);
		}
	}

	public void placeX(int cell) {
		graphics.setStroke(Color.ORANGERED);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x() + gapSize;
		final float y = cells[cell].y() + gapSize;

		final float width = cells[cell].width() - gapSize * 2;
		final float height = cells[cell].height() - gapSize * 2;

		graphics.strokeLine(x, y, x + width, y + height);
		graphics.strokeLine(x + width, y, x, y + height);
	}

	public void placeO(int cell) {
		graphics.setStroke(Color.DEEPSKYBLUE);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x() + gapSize;
		final float y = cells[cell].y() + gapSize;

		final float width = cells[cell].width() - gapSize * 2;
		final float height = cells[cell].height() - gapSize * 2;

		graphics.strokeOval(x, y, width, height);
	}

	@Override
	protected void onCellClicked(int cell, boolean primary) {
		for (final Game.Move move : game.getLegalMoves()) {
			if (move.position() == cell) {
				if (move.value() == 'X') {
					placeX(cell);
				} else {
					placeO(cell);
				}

				final Game.State state = game.play(move);

				if (state == Game.State.WIN) {
					for (int i = 0; i < game.board.length; i++) {
						if (game.board[i] != move.value()) {
							clearCell(i);
						}
					}

					graphics.setFill(Color.GREEN);
					graphics.fillRect(cells[4].x(), cells[4].y(), cells[4].width(), cells[4].height());
				} else if (state == Game.State.DRAW) {
					graphics.setFill(Color.DARKORANGE);
					graphics.fillRect(cells[4].x(), cells[4].y(), cells[4].width(), cells[4].height());
				}
			}
		}
	}
}