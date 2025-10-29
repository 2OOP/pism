package org.toop.game.tictactoe;

import java.util.ArrayList;
import org.toop.game.TurnBasedGame;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;

public final class TicTacToe extends TurnBasedGame {
    private int movesLeft;

    public TicTacToe() {
        super(3, 3, 2);
        movesLeft = this.getBoard().length;
    }

    public TicTacToe(TicTacToe other) {
        super(other);
        movesLeft = other.movesLeft;
    }

    @Override
    public Move[] getLegalMoves() {
        final ArrayList<Move> legalMoves = new ArrayList<>();
        final char currentValue = getCurrentValue();

        for (int i = 0; i < this.getBoard().length; i++) {
            if (this.getBoard()[i] == EMPTY) {
                legalMoves.add(new Move(i, currentValue));
            }
        }

        return legalMoves.toArray(new Move[0]);
    }

    @Override
    public GameState play(Move move) {
        assert move != null;
        assert move.position() >= 0 && move.position() < this.getBoard().length;
        assert move.value() == getCurrentValue();

        // TODO: Make sure this move is allowed, maybe on the board side?
        this.setBoard(move);
        movesLeft--;

        if (checkForWin()) {
            return GameState.WIN;
        }

        nextTurn();

        if (movesLeft <= 2) {
            if (movesLeft <= 0 || checkForEarlyDraw(this)) {
                return GameState.DRAW;
            }
        }

        return GameState.NORMAL;
    }

    private boolean checkForWin() {
        // Horizontal
        for (int i = 0; i < 3; i++) {
            final int index = i * 3;

            if (this.getBoard()[index] != EMPTY
                    && this.getBoard()[index] == this.getBoard()[index + 1]
                    && this.getBoard()[index] == this.getBoard()[index + 2]) {
                return true;
            }
        }

        // Vertical
        for (int i = 0; i < 3; i++) {
            if (this.getBoard()[i] != EMPTY && this.getBoard()[i] == this.getBoard()[i + 3] && this.getBoard()[i] == this.getBoard()[i + 6]) {
                return true;
            }
        }

        // B-Slash
        if (this.getBoard()[0] != EMPTY && this.getBoard()[0] == this.getBoard()[4] && this.getBoard()[0] == this.getBoard()[8]) {
            return true;
        }

        // F-Slash
        return this.getBoard()[2] != EMPTY && this.getBoard()[2] == this.getBoard()[4] && this.getBoard()[2] == this.getBoard()[6];
    }

    private boolean checkForEarlyDraw(TicTacToe game) {
        for (final Move move : game.getLegalMoves()) {
            final TicTacToe copy = new TicTacToe(game);

            if (copy.play(move) == GameState.WIN || !checkForEarlyDraw(copy)) {
                return false;
            }
        }

        return true;
    }

    private char getCurrentValue() {
        return currentTurn == 0 ? 'X' : 'O';
    }
}
