package org.toop.game.games.reversi;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.BitboardGame;

import java.util.Arrays;

public class BitboardReversi extends BitboardGame<BitboardReversi> {

    @Override
    public int[] getBoard() {
        return translateBoard();
    }

    public record Score(int black, int white) {}

	private final long notAFile = 0xfefefefefefefefeL;
	private final long notHFile = 0x7f7f7f7f7f7f7f7fL;

	public BitboardReversi(Player<BitboardReversi>[] players) {
		super(8, 8, 2, players);

		// Black (player 0)
		setPlayerBitboard(0, (1L << (3 + 4 * 8)) | (1L << (4 + 3 * 8)));

		// White (player 1)
		setPlayerBitboard(1, (1L << (3 + 3 * 8)) | (1L << (4 + 4 * 8)));	}

	public long getLegalMoves2() {
		final long player = getPlayerBitboard(getCurrentPlayerIndex());
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
		final long player = getPlayerBitboard(getCurrentPlayerIndex());
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
    public int[] getLegalMoves(){
        System.out.println(Arrays.toString(translateLegalMoves(getLegalMoves2())));
        return translateLegalMoves(getLegalMoves2());
    }

    @Override
    public PlayResult play(int move) {
        return new PlayResult(playBit(translateMove(move)), getCurrentPlayerIndex());
    }

    // TODO: Implement
    @Override
    public BitboardReversi deepCopy() {return this;};

	public GameState playBit(long move) {
		final long flips = getFlips(move);

		long player = getPlayerBitboard(getCurrentPlayerIndex());
		long opponent = getPlayerBitboard(getNextPlayer());

		player |= move | flips;
		opponent &= ~flips;

		setPlayerBitboard(getCurrentPlayerIndex(), player);
		setPlayerBitboard(getNextPlayer(), opponent);

		nextTurn();

		final long nextLegalMoves = getLegalMoves2();

		if (nextLegalMoves == 0) {
			nextTurn();

			final long skippedLegalMoves = getLegalMoves2();

			if (skippedLegalMoves == 0) {
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
		long moves = shift(player, shift, mask) & opponent;
		long captured = moves;

		while (moves != 0) {
			moves = shift(moves, shift, mask) & opponent;
			captured |= moves;
		}

		long landing = shift(captured, shift, mask);
		return landing & ~(player | opponent);
	}

	private long computeFlips(long move, long player, long opponent, int shift, long mask) {
		long flips = 0L;
		long pos = move;

		while (true) {
			pos = shift(pos, shift, mask);
			if (pos == 0) return 0L;

			if ((pos & opponent) != 0) flips |= pos;
			else if ((pos & player) != 0) return flips;
			else return 0L;
		}
	}

	private long shift(long bit, int shift, long mask) {
		return shift > 0 ? (bit << shift) & mask : (bit >>> -shift) & mask;
	}
}