package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.framework.gameFramework.model.game.AbstractGame;
import org.toop.game.Move;
import org.toop.game.games.reversi.ReversiR;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
@Deprecated
public final class ReversiCanvas extends BaseGameCanvas<ReversiR> {
    private Move[] currentlyHighlightedMoves = null;
	public ReversiCanvas(Color color, int width, int height, Consumer<Integer> onCellClicked,  Consumer<Integer> newCellEntered) {
		super(color, new Color(0f,0.4f,0.2f,1f), width, height, 8, 8, 5, true, onCellClicked,  newCellEntered);
		drawStartingDots();

        final AtomicReference<Cell> lastHoveredCell = new AtomicReference<>(null);

        canvas.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            int cellId = -1;

            Cell hovered = null;
            for (Cell cell : cells) {
                if (cell.isInside(mouseX, mouseY)) {
                    hovered = cell;
                    cellId = turnCoordsIntoCellId(mouseX, mouseY);
                    break;
                }
            }

            Cell previous = lastHoveredCell.get();

            if (hovered != previous) {
                lastHoveredCell.set(hovered);
                newCellEntered.accept(cellId);
            }
        });
	}

    public void setCurrentlyHighlightedMovesNull() {
        currentlyHighlightedMoves = null;
    }

    public void drawHighlightDots(Move[] moves){
        if (currentlyHighlightedMoves != null){
            for (final Move move : currentlyHighlightedMoves){
                Color color = move.value() == 'W'? Color.BLACK: Color.WHITE;
                drawInnerDot(color, move.position(), true);
            }
        }
        currentlyHighlightedMoves = moves;
        if (moves != null) {
            for (Move move : moves) {
                Color color = move.value() == 'B' ? Color.BLACK : Color.WHITE;
                drawInnerDot(color, move.position(), false);
            }
        }
    }

    private int turnCoordsIntoCellId(double x, double y) {
        final int column = (int) ((x / this.width) * rowSize);
        final int row = (int) ((y / this.height) * columnSize);
        return column + row * rowSize;
    }

	public void drawStartingDots() {
		drawDot(Color.BLACK, 28);
		drawDot(Color.WHITE, 36);
		drawDot(Color.BLACK, 35);
		drawDot(Color.WHITE, 27);
	}

	public void drawLegalPosition(int cell, char player) {

        Color innerColor;
        if (player == 'B') {
            innerColor = new Color(0.0f, 0.0f, 0.0f, 0.6f);
        }
        else {
            innerColor = new Color(1.0f, 1.0f, 1.0f, 0.75f);
        }
        drawInnerDot(innerColor, cell,false);
	}

    @Override
    public void drawPlayerMove(int player ,int move){
        super.drawPlayerMove(player, move);
    }

    @Override
    public void drawPlayerHover(int player, int move, AbstractGame game) {

    }
}