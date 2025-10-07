package org.toop.app.canvas;

import java.util.function.Consumer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public abstract class GameCanvas {
    protected record Cell(float x, float y, float width, float height) {}

    protected final Canvas canvas;
    protected final GraphicsContext graphics;

    protected final Color color;

    protected int width;
    protected int height;

    protected final int rows;
    protected final int columns;

    protected final int gapSize;
    protected final boolean edges;

    protected final Cell[] cells;

    protected GameCanvas(
            Color color,
            int width,
            int height,
            int rows,
            int columns,
            int gapSize,
            boolean edges,
            Consumer<Integer> onCellClicked) {
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

        final float cellWidth = ((float) width - (rows - 1) * gapSize) / rows;
        final float cellHeight = ((float) height - (columns - 1) * gapSize) / columns;

        for (int y = 0; y < columns; y++) {
            final float startY = y * cellHeight + y * gapSize;

            for (int x = 0; x < rows; x++) {
                final float startX = x * cellWidth + x * gapSize;
                cells[y * rows + x] = new Cell(startX, startY, cellWidth, cellHeight);
            }
        }

        canvas.setOnMouseClicked(
                event -> {
                    if (event.getButton() != MouseButton.PRIMARY) {
                        return;
                    }

                    final int column = (int) ((event.getX() / width) * rows);
                    final int row = (int) ((event.getY() / height) * columns);

                    event.consume();
                    onCellClicked.accept(row * rows + column);
                });

        render();
    }

    public void clear() {
        graphics.clearRect(0, 0, width, height);
    }

    public void render() {
        graphics.setFill(color);

        for (int x = 1; x < rows; x++) {
            graphics.fillRect(cells[x].x() - gapSize, 0, gapSize, height);
        }

        for (int y = 1; y < columns; y++) {
            graphics.fillRect(0, cells[y * rows].y() - gapSize, width, gapSize);
        }

        if (edges) {
            graphics.fillRect(-gapSize, 0, gapSize, height);
            graphics.fillRect(0, -gapSize, width, gapSize);

            graphics.fillRect(width - gapSize, 0, gapSize, height);
            graphics.fillRect(0, height - gapSize, width, gapSize);
        }
    }

    public void draw(Color color, int cell) {
        final float x = cells[cell].x() + gapSize;
        final float y = cells[cell].y() + gapSize;

        final float width = cells[cell].width() - gapSize * 2;
        final float height = cells[cell].height() - gapSize * 2;

        graphics.setFill(color);
        graphics.fillRect(x, y, width, height);
    }

    public void resize(int width, int height) {
        canvas.setWidth(width);
        canvas.setHeight(height);

        this.width = width;
        this.height = height;

        clear();
        render();
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
