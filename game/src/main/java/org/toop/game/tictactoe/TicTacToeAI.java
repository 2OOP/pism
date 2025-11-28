package org.toop.game.tictactoe;

import org.toop.game.AI;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;

public final class TicTacToeAI extends AI<TicTacToe> {
	@Override
	public Move findBestMove(TicTacToe game, int depth) {
		assert game != null;
		assert depth >= 0;

		final Move[] legalMoves = game.getLegalMoves();

		if (legalMoves.length == 0) {
			return null;
		}

		if (legalMoves.length == 9) {
			return switch ((int)(Math.random() * 4)) {
				case 0 -> legalMoves[2];
				case 1 -> legalMoves[6];
				case 2 -> legalMoves[8];
				default -> legalMoves[0];
			};
		}

		int bestScore = -depth;
		Move bestMove = null;

		for (final Move move : legalMoves) {
			final int score = getMoveScore(game, depth, move, true);

			if (score > bestScore) {
				bestMove = move;
				bestScore = score;
			}
		}

		return bestMove != null? bestMove : legalMoves[(int)(Math.random() * legalMoves.length)];
	}
    public Move findWorstMove(TicTacToe game, int depth){


        Move[] legalMoves = game.getLegalMoves();

        int bestScore = -depth;
        Move bestMove = null;

        for (final Move move : legalMoves) {
            final int score = getMoveScore(game, depth, move, false);

            if (score > bestScore) {
                bestMove = move;
                bestScore = score;
            }
        }
        return bestMove;
    }

	private int getMoveScore(TicTacToe game, int depth, Move move, boolean maximizing) {
		final TicTacToe copy = new TicTacToe(game);
		final GameState state = copy.play(move);

		switch (state) {
			case GameState.DRAW: return 0;
			case GameState.WIN: return maximizing? depth + 1 : -depth - 1;
		}

		if (depth <= 0) {
			return 0;
		}

		final Move[] legalMoves = copy.getLegalMoves();
		int score = maximizing? depth + 1 : -depth - 1;

		for (final Move next : legalMoves) {
			if (maximizing) {
				score = Math.min(score, getMoveScore(copy, depth - 1, next, false));
			} else {
				score = Math.max(score, getMoveScore(copy, depth - 1, next, true));
			}
		}

		return score;
	}
}