package org.toop.app.screen;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicTacToeScreen extends Screen {
	public TicTacToeScreen(int size) {
		super(size, size, 3, 3, 10);

		graphics.setFill(Color.CYAN);

		for (int x = 1; x < rowSize; x++) {
			graphics.fillRect(cells[x].x - gapSize, 0, gapSize, height - gapSize);
		}

		for (int y = 1; y < columnSize; y++) {
			graphics.fillRect(0, cells[y * rowSize].y - gapSize, width - gapSize, gapSize);
		}
	}

	public void placeX(int cell) {
		graphics.setFill(Color.WHITE);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x;
		final float y = cells[cell].y;

		final float width = cells[cell].width;
		final float height = cells[cell].height;

		graphics.strokeLine(x, y, x + width, y + height);
		graphics.strokeLine(x + width, y, x, y + height);
	}

	public void placeO(int cell) {
		graphics.setFill(Color.WHITE);
		graphics.setLineWidth(gapSize);

		final float x = cells[cell].x;
		final float y = cells[cell].y;

		final float width = cells[cell].width;
		final float height = cells[cell].height;

		graphics.strokeOval(x, y, width, height);
	}

	public void simulate(int speedInMilliseconds) {
		final Random random = new Random();

		final List<Integer> playedCells = new ArrayList<>();

		final Timeline timeline = new Timeline(new KeyFrame(Duration.millis(speedInMilliseconds), _ ->{
			int cell;

			do {
				cell = random.nextInt(cells.length);
			} while (playedCells.contains(cell));

			if (playedCells.size() % 2 == 0) {
				placeX(cell);
			} else {
				placeO(cell);
			}

			playedCells.add(cell);
		}));

		timeline.setCycleCount(9);
		timeline.play();
	}
}