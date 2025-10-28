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
		fill(new Color(color.getRed() * 0.25, color.getGreen() * 0.25, color.getBlue() * 0.25, 1.0), cell);
		drawDot(new Color(color.getRed() * 0.5, color.getGreen() * 0.5, color.getBlue() * 0.5, 1.0), cell);
	}
}