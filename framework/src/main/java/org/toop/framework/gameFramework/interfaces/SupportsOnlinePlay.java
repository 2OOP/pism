package org.toop.framework.gameFramework.interfaces;

import org.toop.framework.networking.events.NetworkEvents;

/**
 * Interface for games that support online multiplayer play.
 * <p>
 * Methods are called in response to network events from the server.
 */
public interface SupportsOnlinePlay {

    /** Called when it is this player's turn to make a move. */
    void yourTurn(NetworkEvents.YourTurnResponse event);

    /** Called when a move from another player is received. */
    void moveReceived(NetworkEvents.GameMoveResponse event);

    /** Called when the game has finished, with the final result. */
    void gameFinished(NetworkEvents.GameResultResponse event);
}
