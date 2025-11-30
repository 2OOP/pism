package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.game.tictactoe.TicTacToeR;

import java.util.function.Consumer;

public final class TicTacToeCanvas extends GameCanvas<TicTacToeR> {
	public TicTacToeCanvas(Color color, int width, int height, Consumer<Integer> onCellClicked) {
		super(color, Color.TRANSPARENT, width, height, 3, 3, 30, false, onCellClicked,null);
	}

    @Override
    public void drawPlayerMove(int player, int move) {
        switch (player) {
            case 0 -> drawX(Color.RED, move);
            case 1 -> drawO(Color.BLUE, move);
            default -> super.drawPlayerMove(player, move);
        }
    }

	public void drawX(Color color, int cell) {
		graphics.setStroke(color);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x() + gapSize;
		final float y = cells[cell].y() + gapSize;

		final float width = cells[cell].width() - gapSize * 2;
		final float height = cells[cell].height() - gapSize * 2;

		graphics.strokeLine(x, y, x + width, y + height);
		graphics.strokeLine(x + width, y, x, y + height);
	}

	public void drawO(Color color, int cell) {
		graphics.setStroke(color);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x() + gapSize;
		final float y = cells[cell].y() + gapSize;

		final float width = cells[cell].width() - gapSize * 2;
		final float height = cells[cell].height() - gapSize * 2;

		graphics.strokeOval(x, y, width, height);
	}

    @Override
    public void drawPlayerHover(int player, int move, TurnBasedGameR game) {

    }
}