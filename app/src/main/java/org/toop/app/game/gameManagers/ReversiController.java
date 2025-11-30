package org.toop.app.game.gameManagers;

import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.ReversiCanvas;
import org.toop.app.widget.WidgetContainer;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GameR;
import org.toop.framework.gui.GUIEvents;
import org.toop.game.GameThreadBehaviour.LocalFixedRateThreadBehaviour;
import org.toop.game.GameThreadBehaviour.OnlineThreadBehaviour;
import org.toop.game.players.AbstractPlayer;
import org.toop.game.players.LocalPlayer;
import org.toop.game.reversi.ReversiR;

public class ReversiController extends GameController<ReversiR> {
    // TODO: Refactor GUI update methods to follow designed system
    public ReversiController(AbstractPlayer[] players, boolean local) {
        ReversiR ReversiR = new ReversiR();
        super(
                new ReversiCanvas(Color.GRAY, (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {new EventFlow().addPostEvent(GUIEvents.PlayerAttemptedMove.class, c).postEvent();}, (c) -> {new EventFlow().addPostEvent(GUIEvents.PlayerHoverMove.class, c).postEvent();}),
                players,
                ReversiR,
                local ? new LocalFixedRateThreadBehaviour(ReversiR, players) : new OnlineThreadBehaviour(ReversiR, players), // TODO: Player order matters here, this won't work atm
                "Reversi");
        eventFlow.listen(GUIEvents.PlayerAttemptedMove.class, event -> {if (getCurrentPlayer() instanceof LocalPlayer lp){lp.setMove(event.move());}}, false);
        eventFlow.listen(GUIEvents.PlayerHoverMove.class, this::onHoverMove, false);
        initUI();
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

    public ReversiController(AbstractPlayer[] players) {
        this(players, true);
    }

    private void updateCanvas(boolean animate) {
        // Todo: this is very inefficient. still very fast but if the grid is bigger it might cause issues. improve.
        canvas.clearAll();

        for (int i = 0; i < game.getBoard().length; i++) {
            if (game.getBoard()[i] == 0) {
                canvas.drawDot(Color.WHITE, i);
            } else if (game.getBoard()[i] == 1) {
                canvas.drawDot(Color.BLACK, i);
            }
        }

        final int[] flipped = game.getMostRecentlyFlippedPieces();

        final SequentialTransition animation = new SequentialTransition();

        final Color fromColor = getCurrentPlayerIndex() == 0? Color.WHITE : Color.BLACK;
        final Color toColor = getCurrentPlayerIndex() == 0? Color.BLACK : Color.WHITE;

        if (animate && flipped != null) {
            for (final int flip : flipped) {
                canvas.clear(flip);
                canvas.drawDot(fromColor, flip);
                animation.getChildren().addFirst(canvas.flipDot(fromColor, toColor, flip));
            }
        }

        animation.setOnFinished(_ -> {

            if (getCurrentPlayer() instanceof LocalPlayer) {
                final int[] legalMoves = game.getLegalMoves();

                for (final int legalMove : legalMoves) {
                    drawLegalPosition(legalMove, getCurrentPlayerIndex());
                }
            }
        });

        animation.play();
        primary.nextPlayer(true, getCurrentPlayer().getName(), game.getCurrentTurn() == 0 ? "X" : "O", getPlayer((game.getCurrentTurn() + 1) % 2).getName());
    }

    @Override
    public void updateUI() {
        updateCanvas(false);
    }

    public void drawLegalPosition(int cell, int player) {
        Color innerColor;
        if (player == 1) {
            innerColor = new Color(0.0f, 0.0f, 0.0f, 0.6f);
        }
        else {
            innerColor = new Color(1.0f, 1.0f, 1.0f, 0.75f);
        }
        canvas.drawInnerDot(innerColor, cell,false);
    }

    private void initUI(){
        primary.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.getCurrentView().transitionNext(primary, true);
        updateCanvas(false);
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
