package org.toop.game.gameThreads;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.events.NetworkEvents;

/**
 * Online thread behaviour that adds a fixed delay before processing
 * the local player's turn.
 * <p>
 * This is identical to {@link OnlineThreadBehaviour}, but inserts a
 * short sleep before delegating to the base implementation.
 */
public class OnlineWithSleepThreadBehaviour extends OnlineThreadBehaviour {

    /**
     * Creates the behaviour and forwards the players to the base class.
     *
     * @param game the online-capable turn-based game
     */
    public OnlineWithSleepThreadBehaviour(TurnBasedGame game) {
        super(game);
    }

    /**
     * Waits briefly before handling the "your turn" event.
     *
     * @param event the network event indicating it's this client's turn
     */
    @Override
    public void onYourTurn(long clientId) {

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.onYourTurn(clientId);
    }
}
