package org.toop.framework.games;

/**
 * Interface representing a playable game with rules for determining legal moves
 * and executing moves.
 * <p>
 * Any game class implementing this interface should provide methods to query
 * the current legal moves and to apply a move to the game state, returning
 * the resulting game state.
 * </p>
 */
public interface IPlayableR {

    /**
     * Returns an array of legal moves that can currently be played in the game.
     *
     * @return an array of integers representing valid moves; may be empty if no moves are possible
     */
    int[] getLegalMoves();

    /**
     * Applies a move to the game and returns the resulting state.
     *
     * @param move the move to play, represented as an integer
     * @return the {@link GameState} after the move is played
     */
    PlayResult play(int move);
}
