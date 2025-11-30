package org.toop.app.game.gameManagers;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.ReversiCanvas;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.widget.WidgetContainer;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.gameFramework.GameR;
import org.toop.framework.gui.GUIEvents;
import org.toop.game.GameThreadBehaviour.LocalThreadBehaviour;
import org.toop.game.GameThreadBehaviour.OnlineThreadBehaviour;
import org.toop.game.players.AbstractPlayer;
import org.toop.game.players.LocalPlayer;
import org.toop.game.records.Move;
import org.toop.game.reversi.ReversiR;
import org.toop.game.tictactoe.TicTacToeR;

import java.awt.*;

public class ReversiManager extends GameManager<ReversiR> {

    public ReversiManager(AbstractPlayer[] players, boolean local) {
        ReversiR ReversiR = new ReversiR();
        super(
                new ReversiCanvas(Color.GRAY, (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {new EventFlow().addPostEvent(GUIEvents.PlayerAttemptedMove.class, c).postEvent();}, (c) -> {new EventFlow().addPostEvent(GUIEvents.PlayerHoverMove.class, c).postEvent();}),
                players,
                ReversiR,
                local ? new LocalThreadBehaviour(ReversiR, players) : new OnlineThreadBehaviour(ReversiR, players[0]), // TODO: Player order matters here, this won't work atm
                "Reversi");
        initUI();
        addLisener(GlobalEventBus.subscribe(GUIEvents.PlayerAttemptedMove.class, event -> {if (getCurrentPlayer() instanceof LocalPlayer lp){lp.setMove(event.move());}}));
        addLisener(GlobalEventBus.subscribe(GUIEvents.PlayerHoverMove.class, this::onHoverMove));
        start();
        //new EventFlow().listen(GUIEvents.PlayerAttemptedMove.class, event -> {if (getCurrentPlayer() instanceof LocalPlayer lp){lp.setMove(event.move());}});
    }


    private void onHoverMove(GUIEvents.PlayerHoverMove event){
        int cellEntered = event.move();
        canvas.drawPlayerHover(-1, cellEntered, game);
        /*// (information.players[game.getCurrentTurn()].isHuman) {
            int[] legalMoves = game.getLegalMoves();
            boolean isLegalMove = false;
            for (int move : legalMoves) {
                if (move == cellEntered){
                    isLegalMove = true;
                    break;
                }
            }

            if (cellEntered >= 0){
                int[] moves = null;
                if (isLegalMove) {
                    moves = game.getFlipsForPotentialMove(
                            new Point(cellEntered%game.getColumnSize(),cellEntered/game.getRowSize()),
                            game.getCurrentPlayer());
                }
                canvas.drawHighlightDots(moves);
            }
        //}*/
    }

    public ReversiManager(AbstractPlayer[] players) {
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
        WidgetContainer.getCurrentView().transitionNext(primary, true);
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
