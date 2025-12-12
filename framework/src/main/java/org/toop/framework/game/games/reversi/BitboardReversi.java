package org.toop.game.games.reversi;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.BitboardGame;

public class BitboardReversi extends BitboardGame {

    public record Score(int black, int white) {}

	private final long notAFile = 0xfefefefefefefefeL;
	private final long notHFile = 0x7f7f7f7f7f7f7f7fL;

	public BitboardReversi(Player[] players) {
		super(8, 8, 2, players);

		// Black (player 0)
		setPlayerBitboard(0, (1L << (3 + 4 * 8)) | (1L << (4 + 3 * 8)));

		// White (player 1)
		setPlayerBitboard(1, (1L << (3 + 3 * 8)) | (1L << (4 + 4 * 8)));
	}

    public BitboardReversi(BitboardReversi other) {
        super(other);
    }

	public long getLegalMoves() {
		final long player = getPlayerBitboard(getCurrentPlayerIndex());
		final long opponent = getPlayerBitboard(getNextPlayer());

		long legalMoves = 0L;

		// north & south
		legalMoves |= computeMoves(player, opponent, 8, -1L);
		legalMoves |= computeMoves(player, opponent, -8, -1L);

		// east & west
		legalMoves |= computeMoves(player, opponent, 1, notAFile);
		legalMoves |= computeMoves(player, opponent, -1, notHFile);

		// north-east & north-west & south-east & south-west
		legalMoves |= computeMoves(player, opponent, 9, notAFile);
		legalMoves |= computeMoves(player, opponent, 7, notHFile);
		legalMoves |= computeMoves(player, opponent, -7, notAFile);
		legalMoves |= computeMoves(player, opponent, -9, notHFile);

		return legalMoves;
	}

	public long getFlips(long move) {
		final long player = getPlayerBitboard(getCurrentPlayerIndex());
		final long opponent = getPlayerBitboard(getNextPlayer());

		long flips = 0L;

		// north & south
		flips |= computeFlips(move, player, opponent, 8, -1L);
		flips |= computeFlips(move, player, opponent, -8, -1L);

		// east & west
		flips |= computeFlips(move, player, opponent, 1, notAFile);
		flips |= computeFlips(move, player, opponent, -1, notHFile);

		// north-east & north-west & south-east & south-west
		flips |= computeFlips(move, player, opponent, 9, notAFile);
		flips |= computeFlips(move, player, opponent, 7, notHFile);
		flips |= computeFlips(move, player, opponent, -7, notAFile);
		flips |= computeFlips(move, player, opponent, -9, notHFile);

		return flips;
	}

    @Override
    public BitboardReversi deepCopy() {return new BitboardReversi(this);}

	public PlayResult play(long move) {
		final long flips = getFlips(move);

		long player = getPlayerBitboard(getCurrentPlayerIndex());
		long opponent = getPlayerBitboard(getNextPlayer());

		player |= move | flips;
		opponent &= ~flips;

		setPlayerBitboard(getCurrentPlayerIndex(), player);
		setPlayerBitboard(getNextPlayer(), opponent);

		nextTurn();

		final long nextLegalMoves = getLegalMoves();

		if (nextLegalMoves == 0) {
			nextTurn();

			final long skippedLegalMoves = getLegalMoves();

			if (skippedLegalMoves == 0) {
				int winner = getWinner();

				if (winner == -1) {
					return new PlayResult(GameState.DRAW, -1);
				}

				return new PlayResult(GameState.WIN, winner);
			}

			return new PlayResult(GameState.TURN_SKIPPED, getCurrentPlayerIndex());
		}

		return new PlayResult(GameState.NORMAL, getCurrentPlayerIndex());
	}

	public Score getScore() {
		return new Score(
			Long.bitCount(getPlayerBitboard(0)),
			Long.bitCount(getPlayerBitboard(1))
		);
	}

    public int getWinner(){
        final long black = getPlayerBitboard(0);
        final long white = getPlayerBitboard(1);

        final int blackCount = Long.bitCount(black);
        final int whiteCount = Long.bitCount(white);

        if (blackCount == whiteCount){
            return -1;
        }
        else if (blackCount > whiteCount){
            return 0;
        }
        else{
            return 1;
        }
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