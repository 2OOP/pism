package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.game.PlayResult;
import org.toop.game.TurnBasedGameR;
import org.toop.game.enumerators.GameState;
import org.toop.game.players.Player;

public class LocalThreadBehaviour extends ThreadBehaviourBase implements Runnable{
    private final Player[] players;

    public LocalThreadBehaviour(TurnBasedGameR game, Player[] players) {
        super(game);
        this.players = players;
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)){
            new Thread(this).start();
        }
    }

    @Override
    public void stop() {
        isRunning.set(false);
    }

    @Override
    public void run() {
        // Game logic loop
        while(isRunning.get()) {

            // Get current player
            Player currentPlayer = getCurrentPlayer();

            // Get a valid player move
            int move = getValidMove(currentPlayer);

            // Make move
            PlayResult result = game.play(move);

            // Tell controller to update UI
            new EventFlow().addPostEvent(GUIEvents.UpdateGameCanvas.class).asyncPostEvent();

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

    @Override
    public Player getCurrentPlayer() {
        return players[game.getCurrentTurn()];
    }


}
