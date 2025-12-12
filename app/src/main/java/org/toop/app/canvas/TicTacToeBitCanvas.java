package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.framework.game.games.tictactoe.BitboardTicTacToe;

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
        drawMoves(gameCopy.getBoard());
    }

    private void drawMoves(long[] gameBoard){
        loopOverBoard(gameBoard[0], (i) -> drawX(Color.RED, i));
        loopOverBoard(gameBoard[1], (i) -> drawO(Color.BLUE, i));

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
