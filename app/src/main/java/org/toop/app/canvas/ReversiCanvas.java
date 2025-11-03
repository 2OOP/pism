package org.toop.app.canvas;

import javafx.scene.paint.Color;

import java.util.function.Consumer;

public final class ReversiCanvas extends GameCanvas {
	public ReversiCanvas(Color color, int width, int height, Consumer<Integer> onCellClicked) {
		super(color, Color.GREEN, width, height, 8, 8, 5, true, onCellClicked);
		drawStartingDots();
	}

	public void drawStartingDots() {
		drawDot(Color.BLACK, 28);
		drawDot(Color.WHITE, 36);
		drawDot(Color.BLACK, 35);
		drawDot(Color.WHITE, 27);
	}

	public void drawLegalPosition(Color color, int cell) {
		drawDot(new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.25), cell);
	}
}