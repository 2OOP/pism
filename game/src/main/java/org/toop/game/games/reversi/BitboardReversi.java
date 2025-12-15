package org.toop.game.games.reversi;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.BitboardGame;

public class BitboardReversi extends BitboardGame<BitboardReversi> {

    public record Score(int black, int white) {}

	private final long notAFile = 0xfefefefefefefefeL;
	private final long notHFile = 0x7f7f7f7f7f7f7f7fL;

	public BitboardReversi(Player<BitboardReversi>[] players) {
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
		long legalMoves = 0L;

		final long player = getPlayerBitboard(getCurrentPlayerIndex());
		final long opponent = getPlayerBitboard(getNextPlayer());

		final long empty = ~(player | opponent);

		long mask;
		long direction;

		// north
		mask = opponent;
		direction = (player << 8) & mask;

		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;
		legalMoves |= (direction << 8) & empty;

		// south
		mask = opponent;
		direction = (player >>> 8) & mask;

		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;
		legalMoves |= (direction >>> 8) & empty;

		// east
		mask = opponent & notAFile;
		direction = (player << 1) & mask;

		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;
		legalMoves |= (direction << 1) & empty & notAFile;

		// west
		mask = opponent & notHFile;
		direction = (player >>> 1) & mask;

		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;
		legalMoves |= (direction >>> 1) & empty & notHFile;

		// north-east
		mask = opponent & notAFile;
		direction = (player << 9) & mask;

		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;
		legalMoves |= (direction << 9) & empty & notAFile;

		// north-west
		mask = opponent & notHFile;
		direction = (player << 7) & mask;

		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;
		legalMoves |= (direction << 7) & empty & notHFile;

		// south-east
		mask = opponent & notAFile;
		direction = (player >>> 7) & mask;

		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;
		legalMoves |= (direction >>> 7) & empty & notAFile;

		// south-west
		mask = opponent & notHFile;
		direction = (player >>> 9) & mask;

		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;
		legalMoves |= (direction >>> 9) & empty & notHFile;

		return legalMoves;
	}

	public long getFlips(long move) {
		long flips = 0L;

		final long player = getPlayerBitboard(getCurrentPlayerIndex());
		final long opponent = getPlayerBitboard(getNextPlayer());

		long mask;
		long direction;

		// north
		mask = opponent;
		direction = (move << 8) & mask;

		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;
		direction |= (direction << 8) & mask;

		if (((direction << 8) & player) != 0) {
			flips |= direction;
		}

		// south
		mask = opponent;
		direction = (move >>> 8) & mask;

		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;
		direction |= (direction >>> 8) & mask;

		if (((direction >>> 8) & player) != 0) {
			flips |= direction;
		}

		// east
		mask = opponent & notAFile;
		direction = (move << 1) & mask;

		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;
		direction |= (direction << 1) & mask;

		if (((direction << 1) & player) != 0) {
			flips |= direction;
		}

		// west
		mask = opponent & notHFile;
		direction = (move >>> 1) & mask;

		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;
		direction |= (direction >>> 1) & mask;

		if (((direction >>> 1) & player) != 0) {
			flips |= direction;
		}

		// north-east
		mask = opponent & notAFile;
		direction = (move << 9) & mask;

		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;
		direction |= (direction << 9) & mask;

		if (((direction << 9) & player) != 0) {
			flips |= direction;
		}

		// north-west
		mask = opponent & notHFile;
		direction = (move << 7) & mask;

		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;
		direction |= (direction << 7) & mask;

		if (((direction << 7) & player) != 0) {
			flips |= direction;
		}

		// south-east
		mask = opponent & notAFile;
		direction = (move >>> 7) & mask;

		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;
		direction |= (direction >>> 7) & mask;

		if (((direction >>> 7) & player) != 0) {
			flips |= direction;
		}

		// south-west
		mask = opponent & notHFile;
		direction = (move >>> 9) & mask;

		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;
		direction |= (direction >>> 9) & mask;

		if (((direction >>> 9) & player) != 0) {
			flips |= direction;
		}

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
}