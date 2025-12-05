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
    public BitboardTicTacToe(BitboardTicTacToe other) {
        super(other);
    }

    public long getLegalMoves() {
		final long xBitboard = getPlayerBitboard(0);
		final long oBitboard = getPlayerBitboard(1);

		final long taken = (xBitboard | oBitboard);
		return (~taken) & 0x1ffL;
	}

	public PlayResult play(long move) {
        // Player loses if move is invalid
        if ((move & getLegalMoves()) == 0 || Long.bitCount(move) != 1){
            return new PlayResult(GameState.WIN, getNextPlayer());
        }

        // Move is legal, make move
		long playerBitboard = getPlayerBitboard(getCurrentPlayerIndex());
		playerBitboard |= move;

		setPlayerBitboard(getCurrentPlayerIndex(), playerBitboard);

        // Check if current player won
        if (checkWin(playerBitboard)) {
            return new PlayResult(GameState.WIN, getCurrentPlayerIndex());
        }

        // Proceed to next turn
        nextTurn();


        // Check for early draw
		if (getLegalMoves() == 0L || checkEarlyDraw()) {
			return new PlayResult(GameState.DRAW, -1);
		}

        // Nothing weird happened, continue on as normal
		return new PlayResult(GameState.NORMAL, -1);
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
    public BitboardTicTacToe deepCopy() {
        return new BitboardTicTacToe(this);
    }
}