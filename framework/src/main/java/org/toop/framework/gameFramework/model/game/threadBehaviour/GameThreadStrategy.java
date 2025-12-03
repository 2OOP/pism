package org.toop.framework.gameFramework.model.game.threadBehaviour;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractPlayer;
import org.toop.framework.gameFramework.model.player.Player;

/**
 * Strategy interface for controlling game thread behavior.
 * <p>
 * Defines how a game's execution is started, stopped, and which player is active.
 */
public interface GameThreadStrategy<T extends TurnBasedGame<T>> {

    /** Starts the game loop or execution according to the strategy. */
    void start();

    /** Stops the game loop or execution according to the strategy. */
    void stop();

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current active {@link AbstractPlayer}
     */
    Player<T> getCurrentPlayer();

    int getCurrentPlayerIndex();
}
