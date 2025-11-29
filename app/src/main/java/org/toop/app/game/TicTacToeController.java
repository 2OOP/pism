package org.toop.app.game;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.game.Players.LocalPlayer;
import org.toop.app.game.Players.Player;
import org.toop.app.widget.WidgetContainer;
import org.toop.game.tictactoe.TicTacToeR;

public class TicTacToeController extends GameController {

    public TicTacToeController(Player[] players) {
        TicTacToeR ticTacToeR = new TicTacToeR();
        super(new TicTacToeCanvas(Color.GRAY,
                (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {if (players[ticTacToeR.getCurrentTurn()] instanceof LocalPlayer lp) {lp.enqueueMove(c);}}), "TicTacToe");
        // TODO: Deal with this thread better. Can't give it to super because of "this" refence.
        setThread(new TurnBasedGameThread(players, ticTacToeR, this));

        initUI();
    }

    @Override
    public void updateUI() {
        canvas.clearAll();
        drawMoves();
    }

    private void initUI(){
        primary.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.getCurrentView().transitionNext(primary);
    }

    private void drawMoves(){
        int[] board = gameThread.getBoard();

        // Draw each move
        for (int i = 0; i < board.length; i++){
            switch(board[i]){
                case 0 -> canvas.drawChar('X', Color.RED, i);
                case 1 -> canvas.drawChar('O', Color.BLUE, i);
                default -> {}
            }
        }
    }
}
