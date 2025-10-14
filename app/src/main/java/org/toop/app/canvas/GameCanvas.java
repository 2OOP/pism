package org.toop.app.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public abstract class GameCanvas {
	protected record Cell(float x, float y, float width, float height) {
	}

	protected final Canvas canvas;
	protected final GraphicsContext graphics;

	protected final Color color;

	protected final int width;
	protected final int height;

	protected final int rows;
	protected final int columns;

	protected final int gapSize;
	protected final boolean edges;

	protected final Cell[] cells;

	protected GameCanvas(Color color, int width, int height, int rows, int columns, int gapSize, boolean edges, Consumer<Integer> onCellClicked) {
		width += gapSize * 2;
		height += gapSize * 2;

		canvas = new Canvas(width, height);
		graphics = canvas.getGraphicsContext2D();

		this.color = color;

		this.width = width;
		this.height = height;

		this.rows = rows;
		this.columns = columns;

		this.gapSize = gapSize;
		this.edges = edges;

		cells = new Cell[rows * columns];

		final float cellWidth = ((float) width - gapSize * rows) / rows;
		final float cellHeight = ((float) height - gapSize * columns) / columns;

		for (int y = 0; y < columns; y++) {
			final float startY = gapSize + y * cellHeight + y * gapSize;

			for (int x = 0; x < rows; x++) {
				final float startX = gapSize + x * cellWidth + x * gapSize;
				cells[y * rows + x] = new Cell(startX, startY, cellWidth, cellHeight);
			}
		}

		canvas.setOnMouseClicked(event -> {
			if (event.getButton() != MouseButton.PRIMARY) {
				return;
			}

			final int column = (int)((event.getX() / this.width) * rows);
			final int row = (int)((event.getY() / this.height) * columns);

			event.consume();
			onCellClicked.accept(column + row * rows);
		});

		render();
	}

	public void clear() {
		graphics.clearRect(0, 0, width, height);
	}

	public void render() {
		graphics.setFill(color);

		for (int x = 1; x < rows; x++) {
			graphics.fillRect(cells[x].x() - gapSize, 0, gapSize, height + gapSize);
		}

		for (int y = 1; y < columns; y++) {
			graphics.fillRect(0, cells[y * rows].y() - gapSize, width + gapSize, gapSize);
		}

		if (edges) {
			graphics.fillRect(0, 0, gapSize, height + gapSize);
			graphics.fillRect(0, 0, width + gapSize, gapSize);

			graphics.fillRect(width + gapSize, 0, gapSize, height + gapSize * 2);
			graphics.fillRect(0, height + gapSize, width + gapSize * 2, gapSize);
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