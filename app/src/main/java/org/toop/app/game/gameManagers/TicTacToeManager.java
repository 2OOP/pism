package org.toop.app.game.gameManagers;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.game.GameThreadBehaviour.LocalThreadBehaviour;
import org.toop.game.GameThreadBehaviour.OnlineThreadBehaviour;
import org.toop.game.players.LocalPlayer;
import org.toop.game.players.Player;
import org.toop.app.widget.WidgetContainer;
import org.toop.game.tictactoe.TicTacToeR;

public class TicTacToeManager extends GameManager {

    public TicTacToeManager(Player[] players, boolean local) {
        TicTacToeR ticTacToeR = new TicTacToeR();
        super(
                new TicTacToeCanvas(Color.GRAY, (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {
                    System.out.println("TEST123: " + c);new EventFlow().addPostEvent(GUIEvents.PlayerAttemptedMove.class, c).postEvent();}),
                players,
                ticTacToeR,
                local ? new LocalThreadBehaviour(ticTacToeR, players) : new OnlineThreadBehaviour(ticTacToeR, players[0]), // TODO: Player order matters here, this won't work atm
                "TicTacToe");

        initUI();
        start();
        new EventFlow().listen(GUIEvents.PlayerAttemptedMove.class, event -> {if (getCurrentPlayer() instanceof LocalPlayer lp){lp.setMove(event.move());}});
    }

    public TicTacToeManager(Player[] players) {
        this(players, true);
    }

    @Override
    public void updateUI() {
        System.out.println("TicTacToeManager updateUI");
        canvas.clearAll();
        drawMoves();
    }

    private void initUI(){
        primary.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.getCurrentView().transitionNext(primary);
        updateUI();
    }

    private void drawMoves(){
        int[] board = game.getBoard();

        // Draw each move
        for (int i = 0; i < board.length; i++){
            canvas.drawPlayerMove(board[i], i);
        }
    }
}
