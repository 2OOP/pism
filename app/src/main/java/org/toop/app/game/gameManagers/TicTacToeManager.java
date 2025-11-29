package org.toop.app.game.gameManagers;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.gui.GUIEvents;
import org.toop.framework.games.GameR;
import org.toop.game.GameThreadBehaviour.LocalThreadBehaviour;
import org.toop.game.GameThreadBehaviour.OnlineThreadBehaviour;
import org.toop.game.players.LocalPlayer;
import org.toop.game.players.AbstractPlayer;
import org.toop.app.widget.WidgetContainer;
import org.toop.game.tictactoe.TicTacToeR;

public class TicTacToeManager extends GameManager {

    public TicTacToeManager(AbstractPlayer[] players, boolean local) {
        System.out.println("Creating TicTacToeManager, should be an empty game");
        TicTacToeR ticTacToeR = new TicTacToeR();
        super(
                new TicTacToeCanvas(Color.GRAY, (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {new EventFlow().addPostEvent(GUIEvents.PlayerAttemptedMove.class, c).postEvent();}),
                players,
                ticTacToeR,
                local ? new LocalThreadBehaviour(ticTacToeR, players) : new OnlineThreadBehaviour(ticTacToeR, players[0]), // TODO: Player order matters here, this won't work atm
                "TicTacToe");

        initUI();
        start();
        listeners.add(GlobalEventBus.subscribe(GUIEvents.PlayerAttemptedMove.class, event -> {if (getCurrentPlayer() instanceof LocalPlayer lp){lp.setMove(event.move());}}));
        //new EventFlow().listen(GUIEvents.PlayerAttemptedMove.class, event -> {if (getCurrentPlayer() instanceof LocalPlayer lp){lp.setMove(event.move());}});
    }

    public TicTacToeManager(AbstractPlayer[] players) {
        this(players, true);
    }

    @Override
    public void updateUI() {
        canvas.clearAll();
        // TODO: wtf is even this pile of poop temp fix
        primary.nextPlayer(true, getCurrentPlayer().getName(), game.getCurrentTurn() == 0 ? "X" : "O", getPlayer((game.getCurrentTurn() + 1) % 2).getName());
        drawMoves();
    }

    private void initUI(){
        primary.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.setCurrentView(primary);
        updateUI();
    }

    private void drawMoves(){
        int[] board = game.getBoard();

        // Draw each square
        for (int i = 0; i < board.length; i++){
            // If square isn't empty, draw player move
            if (board[i] != GameR.EMPTY){
                canvas.drawPlayerMove(board[i], i);
            }
        }
    }
}
