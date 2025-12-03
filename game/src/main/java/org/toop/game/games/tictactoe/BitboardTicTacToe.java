package org.toop.game.games.tictactoe;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.game.BitboardGame;

public class BitboardTicTacToe extends BitboardGame<BitboardTicTacToe> {
	private final long[] winningLines = {
		0b111000000L, // top row
		0b000111000L, // middle row
		0b000000111L, // bottom row
		0b100100100L, // left column
		0b010010010L, // middle column
		0b001001001L, // right column
		0b100010001L, // diagonal
		0b001010100L  // anti-diagonal
	};

	public BitboardTicTacToe(Player<BitboardTicTacToe>[] players) {
		super(3, 3, 2, players);
	}

    @Override
    public int[] getLegalMoves(){
        return translateLegalMoves(getLegalMoves2());
    }

    @Override
    public PlayResult play(int move) {
        return new PlayResult(play2(translateMove(move)), getCurrentPlayerIndex());
    }

    public long getLegalMoves2() {
		final long xBitboard = getPlayerBitboard(0);
		final long oBitboard = getPlayerBitboard(1);

		final long taken = (xBitboard | oBitboard);
		return (~taken) & 0x1ffL;
	}

	public GameState play2(long move) {
		long playerBitboard = getPlayerBitboard(getCurrentPlayerIndex());
		playerBitboard |= move;

		setPlayerBitboard(getCurrentPlayerIndex(), playerBitboard);
        nextTurn();

		if (checkWin(playerBitboard)) {
			return GameState.WIN;
		}

		if (getLegalMoves2() <= 0L || checkEarlyDraw()) {
			return GameState.DRAW;
		}



		return GameState.NORMAL;
	}

	private boolean checkWin(long board) {
		for (final long line : winningLines) {
			if ((board & line) == line) {
				return true;
			}
		}

		return false;
	}

	private boolean checkEarlyDraw() {
		final long xBitboard = getPlayerBitboard(0);
		final long oBitboard = getPlayerBitboard(1);

		final long taken = (xBitboard | oBitboard);
		final long empty = (~taken) & 0x1FFL;

		for (final long line : winningLines) {
			if (((line & xBitboard) != 0 && (line & oBitboard) != 0)) {
				continue;
			}

			if ((line & empty) != 0) {
				return false;
			}
		}

		return true;
	}

    @Override
    public int[] getBoard() {
        return translateBoard();
    }

    // TODO: Implement
    @Override
    public BitboardTicTacToe deepCopy() {
        return this;
    }
}