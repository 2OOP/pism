package org.toop.app.screen;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public abstract class Screen {
	protected final class Cell {
		public float x;
		public float y;

		public float width;
		public float height;

		public Cell(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;

			this.width = width;
			this.height = height;
		}

		public boolean check(int x, int y) {
			return x >= this.x && y >= this.y && x <= this.x + width && y <= this.y + height;
		}
	}

	protected final Canvas canvas;
	protected final GraphicsContext graphics;

	protected final int width;
	protected final int height;

	protected final int rowSize;
	protected final int columnSize;

	protected final int gapSize;

	protected final Cell[] cells;

	protected Screen(int width, int height, int rowSize, int columnSize, int gapSize) {
		final Canvas canvas = new Canvas(width, height);
		final GraphicsContext graphics = canvas.getGraphicsContext2D();

		this.canvas = canvas;
		this.graphics = graphics;

		this.width = width;
		this.height = height;

		this.rowSize = rowSize;
		this.columnSize = columnSize;

		this.gapSize = gapSize;

		cells = new Cell[rowSize * columnSize];

		final float cellWidth = ((float)width - (rowSize - 1) * gapSize) / rowSize;
		final float cellHeight = ((float)height - (columnSize - 1) * gapSize) / rowSize;

		for (int y = 0; y < columnSize; y++) {
			final float startY = y * cellHeight + y * gapSize;

			for (int x = 0; x < rowSize; x++) {
				final float startX = x * cellWidth + x * gapSize;
				cells[y * rowSize + x] = new Cell(startX, startY, cellWidth, cellHeight);
			}
		}
	}

	public Canvas getCanvas() { return canvas; }
}