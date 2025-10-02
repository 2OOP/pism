package org.toop.app.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;

public abstract class GameCanvas {
	protected record Cell(float x, float y, float width, float height) {}

	protected final int width;
	protected final int height;

	protected final Canvas canvas;
	protected final GraphicsContext graphics;

	protected final int rows;
	protected final int columns;

	protected final int gapSize;

	protected final Cell[] cells;

	protected GameCanvas(int width, int height, int rows, int columns, int gapSize) {
		final Canvas canvas = new Canvas(width, height);
		final GraphicsContext graphics = canvas.getGraphicsContext2D();

		final Cell[] cells = new Cell[rows * columns];

		final float cellWidth = ((float)width - (rows - 1) * gapSize) / rows;
		final float cellHeight = ((float)height - (columns - 1) * gapSize) / columns;

		for (int y = 0; y < columns; y++) {
			final float startY = y * cellHeight + y * gapSize;

			for (int x = 0; x < rows; x++) {
				final float startX = x * cellWidth + x * gapSize;
				cells[y * rows + x] = new Cell(startX, startY, cellWidth, cellHeight);
			}
		}

		canvas.setOnMouseClicked(event -> {
			final MouseButton button = event.getButton();

			if (button != MouseButton.PRIMARY && button != MouseButton.SECONDARY) {
				return;
			}

			final int column = (int)((event.getX() / width) * rows);
			final int row = (int)((event.getY() / height) * columns);

			event.consume();
			onCellClicked(row * rows + column, button == MouseButton.PRIMARY);
		});

		this.width = width;
		this.height = height;

		this.canvas = canvas;
		this.graphics = graphics;

		this.rows = rows;
		this.columns = columns;

		this.gapSize = gapSize;

		this.cells = cells;
	}

	protected void clearCell(int cell) {
		assert cell >= 0 && cell < cells.length;
		graphics.clearRect(cells[cell].x(), cells[cell].y(), cells[cell].width(), cells[cell].height());
	}

	protected abstract void onCellClicked(int cell, boolean primary);

	public Canvas getCanvas() { return canvas; }
}