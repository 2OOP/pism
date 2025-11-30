package org.toop.framework.gameFramework.interfaces;

import org.toop.framework.gameFramework.abstractClasses.GameR;

/**
 * AI interface for selecting the best move in a game.
 *
 * @param <T> the type of game this AI can play, extending {@link GameR}
 */
public interface IAIMoveR<T extends GameR> {

    /**
     * Determines the optimal move for the current player.
     *
     * @param game  the current game state
     * @param depth the search depth for evaluating moves
     * @return an integer representing the chosen move
     */
    int findBestMove(T game, int depth);
}
