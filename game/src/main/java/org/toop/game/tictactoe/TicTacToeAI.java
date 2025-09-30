package org.toop.game.tictactoe;

import org.toop.game.AI;
import org.toop.game.Game;

public final class TicTacToeAI extends AI<TicTacToe> {
	@Override
	public Game.Move findBestMove(TicTacToe game, int depth) {
		assert game != null;
		assert depth >= 0;

		final Game.Move[] legalMoves = game.getLegalMoves();

		if (legalMoves.length <= 0) {
			return null;
		}

		if (legalMoves.length == 9) {
			return switch ((int)(Math.random() * 4)) {
				case 1 -> legalMoves[2];
				case 2 -> legalMoves[6];
				case 3 -> legalMoves[8];
				default -> legalMoves[0];
			};
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

	private int getMoveScore(TicTacToe game, int depth, Game.Move move, boolean maximizing) {
		final TicTacToe copy = new TicTacToe(game);
		final Game.State state = copy.play(move);

		switch (state) {
			case Game.State.DRAW: return 0;
			case Game.State.WIN: return maximizing? depth + 1 : -depth - 1;
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