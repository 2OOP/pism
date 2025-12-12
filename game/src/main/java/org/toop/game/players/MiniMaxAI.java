package org.toop.game.players;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniMaxAI extends AbstractAI {

    private final int maxDepth;
    private final Random random = new Random();

    public MiniMaxAI(int depth) {
        this.maxDepth = depth;
    }

    public MiniMaxAI(MiniMaxAI other) {
        this.maxDepth = other.maxDepth;
    }

    @Override
    public MiniMaxAI deepCopy() {
        return new MiniMaxAI(this);
    }

    @Override
    public long getMove(TurnBasedGame game) {
        long legalMoves = game.getLegalMoves();
        if (legalMoves == 0) return 0;

        List<Long> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;
        int aiPlayer = game.getCurrentTurn();

        long movesLoop = legalMoves;
        while (movesLoop != 0) {
            long move = 1L << Long.numberOfTrailingZeros(movesLoop);
            TurnBasedGame copy = game.deepCopy();
            PlayResult result = copy.play(move);

            int score;
            switch (result.state()) {
                case WIN -> score = (result.player() == aiPlayer ? maxDepth : -maxDepth);
                case DRAW -> score = 0;
                default -> score = getMoveScore(copy, maxDepth - 1, false, aiPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
            }

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }

            movesLoop &= movesLoop - 1;
        }

        long chosenMove = bestMoves.get(random.nextInt(bestMoves.size()));
        return chosenMove;
    }

    /**
     * Recursive minimax with alpha-beta pruning and heuristic evaluation.
     *
     * @param game       Current game state
     * @param depth      Remaining depth
     * @param maximizing True if AI is maximizing, false if opponent
     * @param aiPlayer   AI's player index
     * @param alpha      Alpha value
     * @param beta       Beta value
     * @return score of the position
     */
    private int getMoveScore(TurnBasedGame game, int depth, boolean maximizing, int aiPlayer, int alpha, int beta) {
        long legalMoves = game.getLegalMoves();

        // Terminal state
        PlayResult lastResult = null;
        if (legalMoves == 0) {
            lastResult = new PlayResult(GameState.DRAW, -1);
        }

        // If the game is over or depth limit reached, evaluate
        if (depth <= 0 || legalMoves == 0) {
            if (lastResult != null) return 0;
            return evaluateBoard(game, aiPlayer);
        }

        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        long movesLoop = legalMoves;

        while (movesLoop != 0) {
            long move = 1L << Long.numberOfTrailingZeros(movesLoop);
            TurnBasedGame copy = game.deepCopy();
            PlayResult result = copy.play(move);

            int score;
            switch (result.state()) {
                case WIN -> score = (result.player() == aiPlayer ? depth : -depth);
                case DRAW -> score = 0;
                default -> score = getMoveScore(copy, depth - 1, !maximizing, aiPlayer, alpha, beta);
            }

            if (maximizing) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, bestScore);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, bestScore);
            }

            // Alpha-beta pruning
            if (beta <= alpha) break;

            movesLoop &= movesLoop - 1;
        }

        return bestScore;
    }

    /**
     * Simple heuristic evaluation for Reversi-like games.
     * Positive = good for AI, Negative = good for opponent.
     *
     * @param game     Game state
     * @param aiPlayer AI's player index
     * @return heuristic score
     */
    private int evaluateBoard(TurnBasedGame game, int aiPlayer) {
        long[] board = game.getBoard();
        int aiCount = 0;
        int opponentCount = 0;

        // Count pieces for AI vs opponent
        for (int i = 0; i < board.length; i++) {
            long bits = board[i];
            for (int j = 0; j < 64; j++) {
                if ((bits & (1L << j)) != 0) {
                    // Assume player 0 occupies even indices, player 1 occupies odd
                    if ((i * 64 + j) % game.getPlayerCount() == aiPlayer) aiCount++;
                    else opponentCount++;
                }
            }
        }

        // Mobility (number of legal moves)
        int mobility = Long.bitCount(game.getLegalMoves());

        // Corner control (top-left, top-right, bottom-left, bottom-right)
        int corners = 0;
        long[] cornerMasks = {1L << 0, 1L << 7, 1L << 56, 1L << 63};
        for (long mask : cornerMasks) {
            for (long b : board) {
                if ((b & mask) != 0) corners += 1;
            }
        }

        // Weighted sum
        return (aiCount - opponentCount) + 2 * mobility + 5 * corners;
    }
}
