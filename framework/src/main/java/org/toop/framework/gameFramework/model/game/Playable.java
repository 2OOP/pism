package org.toop.framework.gameFramework.model.game;

import org.toop.framework.gameFramework.GameState;

/**
 * Interface for turn-based games that can be played and queried for legal moves.
 */
public interface Playable {

    /**
     * Returns the moves that are currently valid in the game.
     *
     * @return an array of integers representing legal moves
     */
    int[] getLegalMoves();

    /**
     * Plays the given move and returns the resulting game state.
     *
     * @param move the move to apply
     * @return the {@link GameState} and additional info after the move
     */
    PlayResult play(int move);
}
