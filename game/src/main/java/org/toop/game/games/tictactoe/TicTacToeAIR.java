package org.toop.game.games.tictactoe;

import org.toop.framework.gameFramework.model.player.AbstractAI;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.GameState;

/**
 * AI implementation for playing Tic-Tac-Toe.
 * <p>
 * This AI uses a recursive minimax-like strategy with a limited depth to
 * evaluate moves. It attempts to maximize its chances of winning while
 * minimizing the opponent's opportunities. Random moves are used in the
 * opening or when no clear best move is found.
 * </p>
 */
public class TicTacToeAIR extends AbstractAI<TicTacToeR> {

    /**
     * Determines the best move for the given Tic-Tac-Toe game state.
     * <p>
     * Uses a depth-limited recursive strategy to score each legal move and
     * selects the move with the highest score. If no legal moves are available,
     * returns -1. If multiple moves are equally good, picks one randomly.
     * </p>
     *
     * @param game  the current Tic-Tac-Toe game state
     * @param depth the depth of lookahead for evaluating moves (non-negative)
     * @return the index of the best move, or -1 if no moves are available
     */

    private int depth;

    public TicTacToeAIR(int depth) {
        this.depth = depth;
    }

    public int getMove(TicTacToeR game) {
        assert game != null;
        final int[] legalMoves = game.getLegalMoves();

        // If there are no moves, return -1
        if (legalMoves.length == 0) {
            return -1;
        }

        // If first move, pick a corner
        if (legalMoves.length == 9) {
            return switch ((int)(Math.random() * 4)) {
                case 0 -> legalMoves[2];
                case 1 -> legalMoves[6];
                case 2 -> legalMoves[8];
                default -> legalMoves[0];
            };
        }

        int bestScore = -depth;
        int bestMove = -1;

        // Calculate Move score of each move, keep track what moves had the best score
        for (final int move : legalMoves) {
            final int score = getMoveScore(game, depth, move, true);

            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }
        return bestMove != -1 ? bestMove : legalMoves[(int)(Math.random() * legalMoves.length)];
    }

    /**
     * Recursively evaluates the score of a potential move using a minimax-like approach.
     *
     * @param game       the current Tic-Tac-Toe game state
     * @param depth      remaining depth to evaluate
     * @param move       the move to evaluate
     * @param maximizing true if the AI is to maximize score, false if minimizing
     * @return the score of the move
     */
    private int getMoveScore(TicTacToeR game, int depth, int move, boolean maximizing) {
        final TicTacToeR copy = game.deepCopy();
        final PlayResult result = copy.play(move);

        GameState state = result.state();

        switch (state) {
            case DRAW: return 0;
            case WIN: return maximizing ? depth + 1 : -depth - 1;
        }

        if (depth <= 0) {
            return 0;
        }

        final int[] legalMoves = copy.getLegalMoves();
        int score = maximizing ? depth + 1 : -depth - 1;

        for (final int next : legalMoves) {
            if (maximizing) {
                score = Math.min(score, getMoveScore(copy, depth - 1, next, false));
            } else {
                score = Math.max(score, getMoveScore(copy, depth - 1, next, true));
            }
        }

        return score;
    }
}
