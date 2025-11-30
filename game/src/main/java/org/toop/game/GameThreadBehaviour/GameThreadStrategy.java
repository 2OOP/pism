package org.toop.game.GameThreadBehaviour;

import org.toop.game.players.AbstractPlayer;

/**
 * Strategy interface for controlling game thread behavior.
 * <p>
 * Defines how a game's execution is started, stopped, and which player is active.
 */
public interface GameThreadStrategy {

    /** Starts the game loop or execution according to the strategy. */
    void start();

    /** Stops the game loop or execution according to the strategy. */
    void stop();

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current active {@link AbstractPlayer}
     */
    AbstractPlayer getCurrentPlayer();
}
