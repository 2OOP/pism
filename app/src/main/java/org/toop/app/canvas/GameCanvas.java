package org.toop.app.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public abstract class GameCanvas {
	protected record Cell(float x, float y, float width, float height) {
		public boolean isInside(double x, double y) {
			return x >= this.x && x <= this.x + width &&
				y >= this.y && y <= this.y + height;
		}
	}

	protected final Canvas canvas;
	protected final GraphicsContext graphics;

	protected final Color color;

	protected final int width;
	protected final int height;

	protected final int rowSize;
	protected final int columnSize;

	protected final int gapSize;
	protected final boolean edges;

	protected final Cell[] cells;

	protected GameCanvas(Color color, int width, int height, int rowSize, int columnSize, int gapSize, boolean edges, Consumer<Integer> onCellClicked) {
		canvas = new Canvas(width, height);
		graphics = canvas.getGraphicsContext2D();

		this.color = color;

		this.width = width;
		this.height = height;

		this.rowSize = rowSize;
		this.columnSize = columnSize;

		this.gapSize = gapSize;
		this.edges = edges;

		cells = new Cell[rowSize * columnSize];

		final float cellWidth = ((float)width - gapSize * rowSize - gapSize) / rowSize;
		final float cellHeight = ((float)height - gapSize * columnSize - gapSize) / columnSize;

		for (int y = 0; y < columnSize; y++) {
			final float startY = y * cellHeight + y * gapSize + gapSize;

			for (int x = 0; x < rowSize; x++) {
				final float startX = x * cellWidth + x * gapSize + gapSize;
				cells[x + y * rowSize] = new Cell(startX, startY, cellWidth, cellHeight);
			}
		}

		canvas.setOnMouseClicked(event -> {
			if (event.getButton() != MouseButton.PRIMARY) {
				return;
			}

			final int column = (int)((event.getX() / this.width) * rowSize);
			final int row = (int)((event.getY() / this.height) * columnSize);

			final Cell cell = cells[column + row * rowSize];

			if (cell.isInside(event.getX(), event.getY())) {
				event.consume();
				onCellClicked.accept(column + row * rowSize);
			}
		});

		render();
	}

	public void clear() {
		graphics.clearRect(0, 0, width, height);
	}

	public void render() {
		graphics.setFill(color);

		for (int x = 0; x < rowSize - 1; x++) {
			final float start = cells[x].x + cells[x].width;
			graphics.fillRect(start, gapSize, gapSize, height - gapSize * 2);
		}

		for (int y = 0; y < columnSize - 1; y++) {
			final float start = cells[y * rowSize].y + cells[y * rowSize].height;
			graphics.fillRect(gapSize, start, width - gapSize * 2, gapSize);
		}

		if (edges) {
			graphics.fillRect(0, 0, width, gapSize);
			graphics.fillRect(0, 0, gapSize, height);

			graphics.fillRect(width - gapSize, 0, gapSize, height);
			graphics.fillRect(0, height - gapSize, width, gapSize);
		}
	}

	public void fill(Color color, int cell) {
		final float x = cells[cell].x();
		final float y = cells[cell].y();

		final float width = cells[cell].width();
		final float height = cells[cell].height();

		graphics.setFill(color);
		graphics.fillRect(x, y, width, height);
	}

	public Canvas getCanvas() {
		return canvas;
	}
}