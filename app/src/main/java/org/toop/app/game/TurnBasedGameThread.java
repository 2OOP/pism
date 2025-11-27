package org.toop.app.game;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.GameCanvas;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.view.GameView;
import org.toop.game.TurnBasedGameR;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;
import org.toop.game.tictactoe.TicTacToe;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class TurnBasedGameThread implements Runnable {
    private final Player[] players;         // List of players, can't be changed.
    private final TurnBasedGameR game;       // Reference to game instance

    private final AtomicBoolean isRunning = new AtomicBoolean();

    // TODO: Seperate this from game Thread
    private final GameView primary = new GameView(null, null, null);
    private final TicTacToeCanvas canvas;

    public TurnBasedGameThread(Player[] players, TurnBasedGameR game) {
        // Make sure player list matches expected size
        if (players.length != game.getPlayerCount()){
            throw new IllegalArgumentException("players and game's players must have same length");
        }

        this.players = players;
        this.game = game;

        Thread thread = new Thread(this::run);
        thread.start();

        // UI SHIZ TO MOVE
        canvas = new TicTacToeCanvas(Color.GRAY,
                (App.getHeight() / 4) * 3, (App.getHeight() / 4) * 3,(c) -> {if (players[game.getCurrentTurn()] instanceof LocalPlayer lp) {lp.enqueueMove(c);}});
        primary.add(Pos.CENTER, canvas.getCanvas());
        WidgetContainer.getCurrentView().transitionNext(primary);

    }

    // Move to UI shiz
    private void drawMove(int move) {
        if (game.getCurrentTurn() == 1) canvas.drawX(Color.RED, move);
        else canvas.drawO(Color.BLUE, move);
    }

    public void run() {
        isRunning.set(true);

        // Game logic loop
        while(isRunning.get()) {

            // Get current player
            Player currentPlayer = players[game.getCurrentTurn()];

            // Get this player's valid moves
            Integer[] validMoves = game.getLegalMoves();

            // Get player's move, reask if Move is invalid
            // TODO: Limit amount of retries?
            int move = currentPlayer.getMove();
            while (!Arrays.asList(validMoves).contains(move)) {
                System.out.println("Invalid move");;
                move = currentPlayer.getMove();
            }

            // Make move
            GameState state = game.play(move);
            drawMove(move);

            if (state != GameState.NORMAL) {
                if (state == GameState.WIN) {
                    // Someone won
                } else if (state == GameState.DRAW) {
                    // THere was a draw
                }
                isRunning.set(false);
            }
        }
    }

    private void updateUI(){

    }
}
