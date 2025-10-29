package org.toop.game.Connect4;

import org.toop.game.AI;
import org.toop.game.Game;
import org.toop.game.enumerators.GameState;

public class Connect4AI extends AI<Connect4> {


    public Game.Move findBestMove(Connect4 game, int depth) {
        assert game != null;
        assert depth >= 0;

        final Game.Move[] legalMoves = game.getLegalMoves();

        if (legalMoves.length <= 0) {
            return null;
        }

        int bestScore = -depth;
        Game.Move bestMove = null;

        for (final Game.Move move : legalMoves) {
            final int score = getMoveScore(game, depth, move, true);

            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }

        return bestMove != null? bestMove : legalMoves[(int)(Math.random() * legalMoves.length)];
    }

    private int getMoveScore(Connect4 game, int depth, Game.Move move, boolean maximizing) {
        final Connect4 copy = new Connect4(game);
        final GameState state = copy.play(move);

        switch (state) {
            case GameState.DRAW: return 0;
            case GameState.WIN: return maximizing? depth + 1 : -depth - 1;
        }

        if (depth <= 0) {
            return 0;
        }

        final Game.Move[] legalMoves = copy.getLegalMoves();
        int score = maximizing? depth + 1 : -depth - 1;

        for (final Game.Move next : legalMoves) {
            if (maximizing) {
                score = Math.min(score, getMoveScore(copy, depth - 1, next, false));
            } else {
                score = Math.max(score, getMoveScore(copy, depth - 1, next, true));
            }
        }

        return score;
    }


}
