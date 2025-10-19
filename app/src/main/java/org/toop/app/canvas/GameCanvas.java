package org.toop.app.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.Arrays;
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

		final float cellWidth = ((float)width - gapSize * columnSize - gapSize) / columnSize;
		final float cellHeight = ((float)height - gapSize * rowSize - gapSize) / rowSize;

		for (int y = 0; y < rowSize; y++) {
			final float startY = y * cellHeight + y * gapSize + gapSize;

			for (int x = 0; x < columnSize; x++) {
				final float startX = x * cellWidth + x * gapSize + gapSize;
				cells[x + y * columnSize] = new Cell(startX, startY, cellWidth, cellHeight);
			}
		}
		canvas.setOnMouseClicked(event -> {
			if (event.getButton() != MouseButton.PRIMARY) {
				return;
			}

			final int column = (int)((event.getX() / this.width) * columnSize);
			final int row = (int)((event.getY() / this.height) * rowSize);

			final Cell cell = cells[column + row * columnSize];

			if (cell.isInside(event.getX(), event.getY())) {
				event.consume();
				onCellClicked.accept(column + row * columnSize);
			}
		});

		render();
	}

	public void clear() {
		graphics.clearRect(0, 0, width, height);
	}

    public void clearCell(int cellIndex) {
        Cell cell =  cells[cellIndex];
        graphics.clearRect(cell.x, cell.y, cell.width, cell.height);
    }

	public void render() {
		graphics.setFill(color);

		for (int x = 0; x < columnSize - 1; x++) {
			final float start = cells[x].x + cells[x].width;
			graphics.fillRect(start, gapSize, gapSize, height - gapSize * 2);
		}

		for (int y = 0; y < rowSize; y++) {
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

	public Canvas getCanvas() {
		return canvas;
	}
}