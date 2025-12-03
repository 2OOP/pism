package org.toop.game.reversi;

import org.toop.framework.gameFramework.GameState;
import org.toop.game.BitboardGame;

public class BitboardReversi extends BitboardGame {
	public record Score(int black, int white) {}

	private final long notAFile = 0xfefefefefefefefeL;
	private final long notHFile = 0x7f7f7f7f7f7f7f7fL;

	public BitboardReversi() {
		super(8, 8, 2);

		// Black (player 0)
		setPlayerBitboard(0, (1L << (3 + 4 * 8)) | (1L << (4 + 3 * 8)));

		// White (player 1)
		setPlayerBitboard(1, (1L << (3 + 3 * 8)) | (1L << (4 + 4 * 8)));	}

	@Override
	public long getLegalMoves() {
		final long player = getPlayerBitboard(getCurrentPlayer());
		final long opponent = getPlayerBitboard(getNextPlayer());

		long legalMoves = 0L;
		legalMoves |= computeMoves(player, opponent, 8, -1L);
		legalMoves |= computeMoves(player, opponent, -8, -1L);
		legalMoves |= computeMoves(player, opponent, 1, notAFile);
		legalMoves |= computeMoves(player, opponent, -1, notHFile);
		legalMoves |= computeMoves(player, opponent, 9, notAFile);
		legalMoves |= computeMoves(player, opponent, 7, notHFile);
		legalMoves |= computeMoves(player, opponent, -7, notHFile);
		legalMoves |= computeMoves(player, opponent, -9, notAFile);

		return legalMoves;
	}

	public long getFlips(long move) {
		final long player = getPlayerBitboard(getCurrentPlayer());
		final long opponent = getPlayerBitboard(getNextPlayer());

		long flips = 0L;
		flips |= computeFlips(move, player, opponent, 8, -1L);
		flips |= computeFlips(move, player, opponent, -8, -1L);
		flips |= computeFlips(move, player, opponent, 1, notAFile);
		flips |= computeFlips(move, player, opponent, -1, notHFile);
		flips |= computeFlips(move, player, opponent, 9, notAFile);
		flips |= computeFlips(move, player, opponent, 7, notHFile);
		flips |= computeFlips(move, player, opponent, -7, notHFile);
		flips |= computeFlips(move, player, opponent, -9, notAFile);

		return flips;
	}

	@Override
	public GameState play(long move) {
		final long flips = getFlips(move);

		long player = getPlayerBitboard(getCurrentPlayer());
		long opponent = getPlayerBitboard(getNextPlayer());

		player |= move | flips;
		opponent &= ~flips;

		setPlayerBitboard(getCurrentPlayer(), player);
		setPlayerBitboard(getNextPlayer(), opponent);

		nextTurn();

		final long nextLegalMoves = getLegalMoves();

		if (nextLegalMoves <= 0) {
			nextTurn();

			final long skippedLegalMoves = getLegalMoves();

			if (skippedLegalMoves <= 0) {
				final long black = getPlayerBitboard(0);
				final long white = getPlayerBitboard(1);

				final int blackCount = Long.bitCount(black);
				final int whiteCount = Long.bitCount(white);

				if (blackCount == whiteCount) {
					return GameState.DRAW;
				}

				return GameState.WIN;
			}

			return GameState.TURN_SKIPPED;
		}

		return GameState.NORMAL;
	}

	public Score getScore() {
		return new Score(
			Long.bitCount(getPlayerBitboard(0)),
			Long.bitCount(getPlayerBitboard(1))
		);
	}

	private long computeMoves(long player, long opponent, int shift, long mask) {
		long moves = player;

		while (true) {
			if (shift > 0) moves = (moves << shift) & mask;
			else moves = (moves >>> -shift) & mask;

			long newMoves = moves & opponent;
			if (newMoves == 0) break;
			moves = newMoves;
		}

		if (shift > 0) moves = (moves << shift) & mask;
		else moves = (moves >>> -shift) & mask;

		return moves & ~(player | opponent);
	}

	private long computeFlips(long move, long player, long opponent, int shift, long mask) {
		long flips = 0L;

		long moves = move;

		while (true) {
			if (shift > 0) moves = (moves << shift) & mask;
			else moves = (moves >>> -shift) & mask;

			if ((moves & opponent) != 0) flips |= moves;
			else if ((moves & player) != 0) return flips;
			else return 0L;
		}
	}
}