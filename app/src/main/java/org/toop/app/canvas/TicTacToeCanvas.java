package org.toop.app.canvas;

import javafx.scene.paint.Color;

import java.util.function.Consumer;

public final class TicTacToeCanvas extends GameCanvas {
	public TicTacToeCanvas(Color color, int width, int height, Consumer<Integer> onCellClicked) {
		super(color, width, height, 3, 3, 30, false, onCellClicked);
	}


}