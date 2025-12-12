package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.framework.game.games.reversi.BitboardReversi;

public class ReversiBitCanvas extends BitGameCanvas<BitboardReversi> {
    public ReversiBitCanvas() {
        super(Color.GRAY, new Color(0f, 0.4f, 0.2f, 1f), (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3, 8, 8, 5, true);
        canvas.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            int cellId = -1;

            BitGameCanvas.Cell hovered = null;
            for (BitGameCanvas.Cell cell : cells) {
                if (cell.isInside(mouseX, mouseY)) {
                    hovered = cell;
                    cellId = turnCoordsIntoCellId(mouseX, mouseY);
                    break;
                }
            }
        });
    }

    private int turnCoordsIntoCellId(double x, double y) {
        final int column = (int) ((x / this.width) * rowSize);
        final int row = (int) ((y / this.height) * columnSize);
        return column + row * rowSize;
    }

    @Override
    public void redraw(BitboardReversi gameCopy) {
        clearAll();
        long[] board = gameCopy.getBoard();
        loopOverBoard(board[0], (i) -> drawDot(Color.WHITE, i));
        loopOverBoard(board[1], (i) -> drawDot(Color.BLACK, i));
    }
}
