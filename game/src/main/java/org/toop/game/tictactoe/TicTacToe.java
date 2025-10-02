package org.toop.game.tictactoe;

import org.toop.game.TurnBasedGame;

import java.util.ArrayList;

public final class TicTacToe extends TurnBasedGame {
    private int movesLeft;

    public TicTacToe() {
        super(3, 3, 2);
        movesLeft = board.length;
    }

    public TicTacToe(TicTacToe other) {
        super(other);
        movesLeft = other.movesLeft;
    }

    @Override
    public Move[] getLegalMoves() {
	    final ArrayList<Move> legalMoves = new ArrayList<>();
	    final char currentValue = getCurrentValue();

        for (int i = 0; i < board.length; i++) {
            if (board[i] == EMPTY) {
                legalMoves.add(new Move(i, currentValue));
            }
        }

        return legalMoves.toArray(new Move[0]);
    }

    @Override
    public State play(Move move) {
        assert move != null;
        assert move.position() >= 0 && move.position() < board.length;
        assert move.value() == getCurrentValue();

        board[move.position()] = move.value();
        movesLeft--;

        if (checkForWin()) {
            return State.WIN;
        }

		nextTurn();

        if (movesLeft <= 2) {
			if (movesLeft <= 0 || checkForEarlyDraw(this)) {
				return State.DRAW;
			}
        }

        return State.NORMAL;
    }

    private boolean checkForWin() {
        // Horizontal
        for (int i = 0; i < 3; i++) {
            final int index = i * 3;

            if (board[index] != EMPTY && board[index] == board[index + 1] && board[index] == board[index + 2]) {
                return true;
            }
        }

        // Vertical
        for (int i = 0; i < 3; i++) {
            if (board[i] != EMPTY && board[i] == board[i + 3] && board[i] == board[i + 6]) {
                return true;
            }
        }

        // B-Slash
        if (board[0] != EMPTY && board[0] == board[4] && board[0] == board[8]) {
            return true;
        }

        // F-Slash
        return board[2] != EMPTY && board[2] == board[4] && board[2] == board[6];
    }

    private boolean checkForEarlyDraw(TicTacToe game) {
        for (final Move move : game.getLegalMoves()) {
	        final TicTacToe copy = new TicTacToe(game);

            if (copy.play(move) == State.WIN || !checkForEarlyDraw(copy)) {
                return false;
            }
        }

        return true;
    }

	private char getCurrentValue() {
		return currentTurn == 0? 'X' : 'O';
	}
}