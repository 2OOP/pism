package org.toop.app.game;

import org.toop.app.game.Players.Player;

import org.toop.game.PlayResult;
import org.toop.game.TurnBasedGameR;
import org.toop.game.enumerators.GameState;

import java.util.concurrent.atomic.AtomicBoolean;

public class TurnBasedGameThread implements Runnable {
    private final Player[] players;         // List of players, can't be changed.
    private final TurnBasedGameR game;       // Reference to game instance

    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final GameController controller;

    public TurnBasedGameThread(Player[] players, TurnBasedGameR game,  GameController controller) {
        // Make sure player list matches expected size
        if (players.length != game.getPlayerCount()){
            throw new IllegalArgumentException("players and game's players must have same length");
        }

        // Set vars
        this.controller = controller;
        this.players = players;
        this.game = game;

        // Create and run thread
        Thread thread = new Thread(this::run);
        thread.start();
    }

    public int[] getBoard(){
        return game.getBoard();
    }

    public Player[] getPlayers() {
        return players;
    }

    public void run() {
        // Game logic loop
        while(isRunning.get()) {

            // Get current player
            Player currentPlayer = players[game.getCurrentTurn()];

            // Get this player's valid moves
            int[] validMoves = game.getLegalMoves();

            // Make sure provided move is valid
            // TODO: Limit amount of retries?
            int move = currentPlayer.getMove(game.clone());
            while (!contains(validMoves, move)) {
                move = currentPlayer.getMove(game.clone());
            }

            // Make move
            PlayResult result = game.play(move);

            // Tell controller to update UI
            controller.updateUI();
            GameState state = result.state();
            if (state != GameState.NORMAL) {
                if (state == GameState.WIN) {
                    // Win, do something
                    System.out.println(result.winner() + " WON");
                } else if (state == GameState.DRAW) {
                    // Draw, do something
                    System.out.println("DRAW");
                }
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
