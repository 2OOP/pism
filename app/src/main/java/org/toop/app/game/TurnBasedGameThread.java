package org.toop.app.game;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import org.toop.app.App;
import org.toop.app.canvas.GameCanvas;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.game.Players.LocalPlayer;
import org.toop.app.game.Players.Player;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.view.GameView;
import org.toop.game.TurnBasedGameR;
import org.toop.game.enumerators.GameState;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class TurnBasedGameThread implements Runnable {
    private final Player[] players;         // List of players, can't be changed.
    private final TurnBasedGameR game;       // Reference to game instance

    private final AtomicBoolean isRunning = new AtomicBoolean();
    //private final GameController controller;

    protected final GameView primary = new GameView(null, null, null);
    protected final GameCanvas canvas;

    public TurnBasedGameThread(Player[] players, TurnBasedGameR game) {
        // Set reference to controller
        //this.controller = controller;

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

    public Player[] getPlayers() {
        return players;
    }

    // Move to UI shiz
    private void drawMove(int move) {
        if (game.getCurrentTurn() == 1){
            canvas.drawChar('X', Color.RED, move);
        }
        else{
            canvas.drawChar('O', Color.RED, move);
        }
    }

    public void run() {
        isRunning.set(true);

        // Game logic loop
        while(isRunning.get()) {

            // Get current player
            Player currentPlayer = players[game.getCurrentTurn()];
            System.out.println(game.getCurrentTurn() + "'s turn");
            // Get this player's valid moves
            int[] validMoves = game.getLegalMoves();

            // Get player's move, reask if Move is invalid
            // TODO: Limit amount of retries?
            int move = currentPlayer.getMove(game.clone());
            while (!contains(validMoves, move)) {
                move = currentPlayer.getMove(game.clone());
            }
            // Make move
            System.out.println(Arrays.toString(game.getBoard()));
            GameState state = game.play(move);
            drawMove(move);

            if (state != GameState.NORMAL) {
                if (state == GameState.WIN) {
                    // Someone won
                } else if (state == GameState.DRAW) {
                    // THere was a draw
                }
                System.out.println(state);
                isRunning.set(false);
            }
        }
    }
    

    // helper function, would like to replace to get rid of this method
    public static boolean contains(int[] array, int value){
        for (int i : array) if (i == value) return true;
        return false;
    }
}
