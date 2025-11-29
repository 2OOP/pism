package org.toop.app.game.gameManagers;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.game.players.LocalPlayer;
import org.toop.game.players.Player;
import org.toop.game.TurnBasedGameController;
import org.toop.app.widget.WidgetContainer;
import org.toop.game.tictactoe.TicTacToeR;

public class TicTacToeManager extends GameManager {

    public TicTacToeManager(Player[] players) {
        TicTacToeR ticTacToeR = new TicTacToeR();
        super(new TicTacToeCanvas(Color.GRAY, (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {if (players[ticTacToeR.getCurrentTurn()] instanceof LocalPlayer lp) {lp.enqueueMove(c);}}),
                new TurnBasedGameController(players, ticTacToeR),
                "TicTacToe");

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
        updateUI();
    }

    private void drawMoves(){
        int[] board = gameThread.getBoard();

        // Draw each move
        for (int i = 0; i < board.length; i++){
            canvas.drawPlayerMove(board[i], i);
        }
    }
}
