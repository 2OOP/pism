package org.toop.app.canvas;

import javafx.scene.paint.Color;

import java.util.function.Consumer;

public final class ReversiCanvas extends GameCanvas {
	public ReversiCanvas(Color color, int width, int height, Consumer<Integer> onCellClicked) {
		super(color, width, height, 8, 8, 10, true, onCellClicked);
		drawStartingDots();
	}

	public void drawDot(Color color, int cell) {
		final float x = cells[cell].x() + gapSize;
		final float y = cells[cell].y() + gapSize;

		final float width = cells[cell].width() - gapSize * 2;
		final float height = cells[cell].height() - gapSize * 2;

		graphics.setFill(color);
		graphics.fillOval(x, y, width, height);
	}

	public void drawStartingDots() {
		drawDot(Color.BLACK, 28);
		drawDot(Color.WHITE, 36);
		drawDot(Color.BLACK, 35);
		drawDot(Color.WHITE, 27);
	}

	public void drawLegalPosition(int cell) {
		drawDot(new Color(1.0f, 0.0f, 0.0f, 0.5f), cell);
	}
}