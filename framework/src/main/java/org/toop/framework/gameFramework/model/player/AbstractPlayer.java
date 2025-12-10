package org.toop.framework.gameFramework.model.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

/**
 * Base class for players in a turn-based game.
 *
 * @param <T> the game type
 */
public abstract class AbstractPlayer<T extends TurnBasedGame<T>> implements Player<T> {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String name;

    /**
     * Creates a new player with the given name.
     *
     * @param name the player name
     */
    protected AbstractPlayer(String name) {
        this.name = name;
    }

    /**
     * Creates a copy of another player.
     *
     * @param other the player to copy
     */
    protected AbstractPlayer(AbstractPlayer<T> other) {
        this.name = other.name;
    }

    /**
     * Gets the player's move for the given game state.
     * A deep copy is provided so the player cannot modify the real state.
     * <p>
     * This method uses the Template Method Pattern: it defines the fixed
     * algorithm and delegates the variable part to {@link #determineMove(T)}.
     *
     * @param game the current game
     * @return the chosen move
     */
    public final long getMove(T game) {
        return determineMove(game.deepCopy());
    }


    /**
     * Determines the player's move using a safe copy of the game.
     * <p>
     * This method is called by {@link #getMove(T)} and should contain
     * the player's strategy for choosing a move.
     *
     * @param gameCopy a deep copy of the game
     * @return the chosen move
     */
    protected abstract long determineMove(T gameCopy);


    /**
     * Returns the player's name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
}
