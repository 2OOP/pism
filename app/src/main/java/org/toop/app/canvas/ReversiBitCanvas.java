package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.game.games.reversi.BitboardReversi;

import java.util.Arrays;
import java.util.function.Consumer;

public class ReversiBitCanvas extends BitGameCanvas<BitboardReversi>{
    public ReversiBitCanvas() {
        super(Color.GRAY, new Color(0f,0.4f,0.2f,1f), (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3, 8, 8, 5, true);
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
        int[] board = translateBoard(gameCopy.getBoard());
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                drawDot(Color.WHITE, i);
            } else if (board[i] == 1) {
                drawDot(Color.BLACK, i);
            }
        }
    }
}
