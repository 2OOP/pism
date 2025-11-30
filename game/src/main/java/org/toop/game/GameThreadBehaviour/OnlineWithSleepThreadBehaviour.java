package org.toop.game.GameThreadBehaviour;

import org.toop.framework.gameFramework.abstractClasses.TurnBasedGameR;
import org.toop.framework.networking.events.NetworkEvents;
import org.toop.game.players.AbstractPlayer;

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
     * @param game    the online-capable turn-based game
     * @param players the list of local and remote players
     */
    public OnlineWithSleepThreadBehaviour(TurnBasedGameR game, AbstractPlayer[] players) {
        super(game, players);
    }

    /**
     * Waits briefly before handling the "your turn" event.
     *
     * @param event the network event indicating it's this client's turn
     */
    @Override
    public void yourTurn(NetworkEvents.YourTurnResponse event) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        super.yourTurn(event);
    }
}
