package org.toop.game.interfaces;

import org.toop.game.GameR;

/**
 * Interface defining the behavior of an AI capable of selecting the best move
 * in a game represented by {@link GameR}.
 *
 * @param <T> the specific type of game this AI can play, extending {@link GameR}
 */
public interface IAIMoveR<T extends GameR> {

    /**
     * Determines the best move for the given game state.
     * <p>
     * Implementations of this method should analyze the provided game state and
     * return the most optimal move for the current player. The analysis can
     * consider future moves up to the specified depth.
     * </p>
     *
     * @param game  the current game state to analyze
     * @param depth the search depth or lookahead for evaluating moves
     * @return an integer representing the chosen move
     */
    int findBestMove(T game, int depth);
}
