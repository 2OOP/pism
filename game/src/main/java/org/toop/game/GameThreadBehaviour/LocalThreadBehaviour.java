package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.framework.gameFramework.PlayResult;
import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.framework.gameFramework.GameState;
import org.toop.game.players.AbstractPlayer;

public class LocalThreadBehaviour extends ThreadBehaviourBase implements Runnable{
    private final AbstractPlayer[] players;

    public LocalThreadBehaviour(TurnBasedGameR game, AbstractPlayer[] players) {
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
            AbstractPlayer currentPlayer = getCurrentPlayer();

            // Get a valid player move
            int move = getValidMove(currentPlayer);

            // Make move
            PlayResult result = game.play(move);

            // Tell controller to update UI
            new EventFlow().addPostEvent(GUIEvents.UpdateGameCanvas.class).postEvent();

            GameState state = result.state();
            if (state != GameState.NORMAL) {
                new EventFlow().addPostEvent(GUIEvents.GameFinished.class, state == GameState.WIN, result.winner()).postEvent();
                System.out.println("test321");
                isRunning.set(false);
            }
        }
    }

    @Override
    public AbstractPlayer getCurrentPlayer() {
        return players[game.getCurrentTurn()];
    }


}
