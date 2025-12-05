package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.game.games.tictactoe.BitboardTicTacToe;

import java.util.Arrays;

public class TicTacToeBitCanvas extends BitGameCanvas<BitboardTicTacToe>{
    public TicTacToeBitCanvas() {
        super(
                Color.GRAY,
                Color.TRANSPARENT,
                (App.getHeight() / 4) * 3,
                (App.getHeight() / 4) * 3,
                3,
                3,
                30,
                false
        );
    }

    @Override
    public void redraw(BitboardTicTacToe gameCopy) {
        clearAll();
        drawMoves(translateBoard(gameCopy.getBoard()));
    }

    private void drawMoves(int[] gameBoard){
        // Draw each square
        for (int i = 0; i < 9; i++){
            // If square isn't empty, draw player move
            if (gameBoard[i] != -1){
                drawPlayerMove(gameBoard[i], i);
            }
        }
    }

    public void drawPlayerMove(int player, int move) {
        switch (player) {
            case 0 -> drawX(Color.RED, move);
            case 1 -> drawO(Color.BLUE, move);
            default -> super.drawPlayerMove(player, move);
        }
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
}
