package org.toop.game.tictactoe;

import org.toop.game.TurnBasedGameR;
import org.toop.game.enumerators.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public final class TicTacToeR extends TurnBasedGameR {
    private int movesLeft;

    public TicTacToeR() {
        super(3, 3, 2);
        movesLeft = this.getBoard().length;
    }

    public TicTacToeR(TicTacToeR other) {
        super(other);
        movesLeft = other.movesLeft;
    }

    @Override
    public int[] getLegalMoves() {
        final ArrayList<Integer> legalMoves = new ArrayList<Integer>();
        final char currentValue = getCurrentValue();

        for (int i = 0; i < this.getBoard().length; i++) {
            if (Objects.equals(this.getBoard()[i], EMPTY)) {
                legalMoves.add(i);
            }
        }
        System.out.println(Arrays.toString(legalMoves.stream().mapToInt(Integer::intValue).toArray()));
        return legalMoves.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public GameState play(int move) {
        assert move >= 0 && move < this.getBoard().length;

        // TODO: Make sure this move is allowed, maybe on the board side?
        this.setBoard(move);
        movesLeft--;
        nextTurn();

        if (checkForWin()) {
            return GameState.WIN;
        }

        if (movesLeft <= 2) {
            if (movesLeft <= 0 || checkForEarlyDraw()) {
                return GameState.DRAW;
            }
        }

        return GameState.NORMAL;
    }

    private boolean checkForWin() {
        // Horizontal
        for (int i = 0; i < 3; i++) {
            final int index = i * 3;

            if (!Objects.equals(this.getBoard()[index], EMPTY)
                    && Objects.equals(this.getBoard()[index], this.getBoard()[index + 1])
                    && Objects.equals(this.getBoard()[index], this.getBoard()[index + 2])) {
                return true;
            }
        }

        // Vertical
        for (int i = 0; i < 3; i++) {
            if (!Objects.equals(this.getBoard()[i], EMPTY) && Objects.equals(this.getBoard()[i], this.getBoard()[i + 3]) && Objects.equals(this.getBoard()[i], this.getBoard()[i + 6])) {
                return true;
            }
        }

        // B-Slash
        if (!Objects.equals(this.getBoard()[0], EMPTY) && Objects.equals(this.getBoard()[0], this.getBoard()[4]) && Objects.equals(this.getBoard()[0], this.getBoard()[8])) {
            return true;
        }

        // F-Slash
        return !Objects.equals(this.getBoard()[2], EMPTY) && Objects.equals(this.getBoard()[2], this.getBoard()[4]) && Objects.equals(this.getBoard()[2], this.getBoard()[6]);
    }

    private boolean checkForEarlyDraw() {
        for (final int move : this.getLegalMoves()) {
            final TicTacToeR copy = this.clone();

            if (copy.play(move) == GameState.WIN || !copy.checkForEarlyDraw()) {
                return false;
            }
        }

        return true;
    }

    private char getCurrentValue() {
        return this.getCurrentTurn() == 0 ? 'X' : 'O';
    }

    @Override
    public TicTacToeR clone() {
        return new TicTacToeR(this);
    }
}
