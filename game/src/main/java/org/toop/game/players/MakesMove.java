package org.toop.game.players;

import org.toop.game.GameR;

/**
 * Interface representing an entity capable of making a move in a game.
 * <p>
 * Any class implementing this interface should provide logic to determine
 * the next move given a snapshot of the current game state.
 * </p>
 */
public interface MakesMove {

    /**
     * Determines the next move based on the provided game state.
     *
     * @param gameCopy a copy or snapshot of the current game state
     *                 (never null)
     * @return an integer representing the chosen move.
     *         The interpretation of this value depends on the specific game.
     */
    int getMove(GameR gameCopy);
}
