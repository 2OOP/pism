package org.toop.app.canvas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.view.GUIEvents;
import org.toop.game.BitboardGame;

import java.util.Arrays;
import java.util.function.Consumer;

public abstract class BitGameCanvas<T extends BitboardGame<T>> implements GameCanvas<T> {
	protected record Cell(float x, float y, float width, float height) {
		public boolean isInside(double x, double y) {
			return x >= this.x && x <= this.x + width &&
				y >= this.y && y <= this.y + height;
		}
	}

	protected final Canvas canvas;
	protected final GraphicsContext graphics;

	protected final Color color;
	protected final Color backgroundColor;

	protected final int width;
	protected final int height;

	protected final int rowSize;
	protected final int columnSize;

	protected final int gapSize;
	protected final boolean edges;

	protected final Cell[] cells;

    private Consumer<Integer> onCellCLicked;

    public void setOnCellClicked(Consumer<Integer> onClick) {
        this.onCellCLicked = onClick;
    }

	protected BitGameCanvas(Color color, Color backgroundColor, int width, int height, int rowSize, int columnSize, int gapSize, boolean edges) {
		canvas = new Canvas(width, height);
		graphics = canvas.getGraphicsContext2D();

        this.onCellCLicked = (c) -> new EventFlow().addPostEvent(GUIEvents.PlayerAttemptedMove.class, c).postEvent();

		this.color = color;
		this.backgroundColor = backgroundColor;

		this.width = width;
		this.height = height;

		this.rowSize = rowSize;
		this.columnSize = columnSize;

		this.gapSize = gapSize;
		this.edges = edges;

		cells = new Cell[rowSize * columnSize];

		final float cellWidth = ((float) width - gapSize * rowSize - gapSize) / rowSize;
		final float cellHeight = ((float) height - gapSize * columnSize - gapSize) / columnSize;

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

			final int column = (int) ((event.getX() / this.width) * rowSize);
			final int row = (int) ((event.getY() / this.height) * columnSize);

			final Cell cell = cells[column + row * rowSize];

			if (cell.isInside(event.getX(), event.getY())) {
				event.consume();
				this.onCellCLicked.accept(column + row * rowSize);
			}
		});




		render();
	}

    public void loopOverBoard(long bb, Consumer<Integer> onCell){
        while (bb != 0) {
            int idx = Long.numberOfTrailingZeros(bb); // index of least-significant 1-bit
            onCell.accept(idx);

            bb &= bb - 1; // clear LSB 1-bit
        }
    }

	private void render() {
		graphics.setFill(backgroundColor);
		graphics.fillRect(0, 0, width, height);

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

	public void clear(int cell) {
		final float x = cells[cell].x();
		final float y = cells[cell].y();

		final float width = cells[cell].width();
		final float height = cells[cell].height();

		graphics.clearRect(x, y, width, height);

		graphics.setFill(backgroundColor);
		graphics.fillRect(x, y, width, height);
	}

	public void clearAll() {
		for (int i = 0; i < cells.length; i++) {
			clear(i);
		}
	}

    public void drawPlayerMove(int player, int move) {
        final float x = cells[move].x() + gapSize;
        final float y = cells[move].y() + gapSize;

        final float width = cells[move].width() - gapSize * 2;
        final float height = cells[move].height() - gapSize * 2;

        graphics.setFill(color);
        graphics.setFont(Font.font("Arial", 40)); // TODO different font and size
        graphics.fillText(String.valueOf(player), x + width, y + height);
    }

	public void drawDot(Color color, int cell) {
		final float x = cells[cell].x() + gapSize;
		final float y = cells[cell].y() + gapSize;

		final float width = cells[cell].width() - gapSize * 2;
		final float height = cells[cell].height() - gapSize * 2;

		graphics.setFill(color);
		graphics.fillOval(x, y, width, height);
	}

    public void drawInnerDot(Color color, int cell, boolean slightlyBigger) {
        final float x = cells[cell].x() + gapSize;
        final float y = cells[cell].y() + gapSize;

        float multiplier = slightlyBigger?1.4f:1.5f;

        final float width = (cells[cell].width() - gapSize * 2)/multiplier;
        final float height = (cells[cell].height() - gapSize * 2)/multiplier;

        float offset = slightlyBigger?5f:4f;

        graphics.setFill(color);
        graphics.fillOval(x + width/offset, y + height/offset, width, height);
    }

	private void drawDotScaled(Color color, int cell, double scale) {
		final float cx = cells[cell].x() + gapSize;
		final float cy = cells[cell].y() + gapSize;

		final float fullWidth = cells[cell].width() - gapSize * 2;
		final float height = cells[cell].height() - gapSize * 2;

		final float scaledWidth = (float)(fullWidth * scale);
		final float offsetX = (fullWidth - scaledWidth) / 2;

		graphics.setFill(color);
		graphics.fillOval(cx + offsetX, cy, scaledWidth, height);
	}

	public Timeline flipDot(Color fromColor, Color toColor, int cell) {
		final int steps = 60;
		final long duration = 250;
		final double interval = duration / (double) steps;

		final Timeline timeline = new Timeline();

		for (int i = 0; i <= steps; i++) {
			final double t = i / (double) steps;
			final KeyFrame keyFrame = new KeyFrame(Duration.millis(i * interval),
				_ -> {
					clear(cell);

					final double scale = t <= 0.5 ? 1 - 2 * t : 2 * t - 1;
					final Color currentColor = t < 0.5 ? fromColor : toColor;

					drawDotScaled(currentColor, cell, scale);
				}
			);

			timeline.getKeyFrames().add(keyFrame);
		}

		return timeline;
	}

	public Canvas getCanvas() {
		return canvas;
	}
}