package org.toop.app.canvas;

import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.game.players.LocalPlayer;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public class ReversiBitCanvas extends BitGameCanvas {
    private TurnBasedGame gameCopy;
    private int previousCell;
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
            if (hovered != null) {
                checkHoverDots(hovered, cellId);
            }
        });
    }

    private int turnCoordsIntoCellId(double x, double y) {
        final int column = (int) ((x / this.width) * rowSize);
        final int row = (int) ((y / this.height) * columnSize);
        return column + row * rowSize;
    }

    @Override
    public void redraw(TurnBasedGame gameCopy) {
        this.gameCopy = gameCopy;
        clearAll();
        long[] board = gameCopy.getBoard();
        loopOverBoard(board[0], (i) -> drawDot(Color.WHITE, i));
        loopOverBoard(board[1], (i) -> drawDot(Color.BLACK, i));
    }

    public void drawLegalDots(TurnBasedGame gameCopy){
        long legal = gameCopy.getLegalMoves();
        loopOverBoard(legal, (i) -> drawInnerDot(gameCopy.getCurrentTurn()==0?new Color(1f,1f,1f,0.65f) :new Color(0f,0f,0f,0.65f), i,false));
    }

    private void checkHoverDots(BitGameCanvas.Cell hovered, int cellId){
        if (previousCell == cellId){
            return;
        }
        long backflips = ((BitboardReversi)gameCopy).getFlips(1L << previousCell);
        loopOverBoard(backflips, (i) -> drawInnerDot(gameCopy.getCurrentTurn()==1?Color.WHITE:Color.BLACK, i,true));
        previousCell = cellId;
        if (gameCopy.getPlayer(gameCopy.getCurrentTurn()) instanceof LocalPlayer) {
            long legal = gameCopy.getLegalMoves();
            if ((legal & (1L << cellId)) != 0) {
                long flips = ((BitboardReversi) gameCopy).getFlips(1L << cellId);
                loopOverBoard(flips, (i) -> drawInnerDot(gameCopy.getCurrentTurn() == 0 ? Color.WHITE : Color.BLACK, i, false));
            }
        }
    }
}
