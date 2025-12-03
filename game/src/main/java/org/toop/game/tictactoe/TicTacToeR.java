package org.toop.game.tictactoe;

import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.game.AbstractGame;
import org.toop.framework.gameFramework.GameState;

import java.util.ArrayList;
import java.util.Objects;

public final class TicTacToeR extends AbstractGame<TicTacToeR> {
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

        for (int i = 0; i < this.getBoard().length; i++) {
            if (Objects.equals(this.getBoard()[i], EMPTY)) {
                legalMoves.add(i);
            }
        }
        return legalMoves.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    public PlayResult play(int move) {
        // NOT MY ASSERTIONS - Stef
        assert move >= 0 && move < this.getBoard().length;

        // Player loses if move is invalid
        if (!contains(getLegalMoves(), move)) {
            // Next player wins
            return new PlayResult(GameState.WIN, (getCurrentTurn() + 1)%2); // TODO: Make this a generic method like getNextPlayer() or something similar.
        }

        // Move is valid, make move.
        this.setBoard(move);
        movesLeft--;
        nextTurn();

        // Check if current player won TODO: Make this generic?
        // Not sure why I am checking for ANY win when only current player should be able to win.
        int t = checkForWin();
        if (t != EMPTY) {
            return new PlayResult(GameState.WIN, t);
        }

        // Check for (early) draw
        if (movesLeft <= 3) {
            if (checkForEarlyDraw()) {
                return new PlayResult(GameState.DRAW, EMPTY);
            }
        }

        // Nothing weird happened, continue on as normal
        return new PlayResult(GameState.NORMAL, EMPTY);
    }

    private int checkForWin() {
        // Horizontal
        for (int i = 0; i < 3; i++) {

            final int index = i * 3;

            if (!Objects.equals(this.getBoard()[index], EMPTY)
                    && Objects.equals(this.getBoard()[index], this.getBoard()[index + 1])
                    && Objects.equals(this.getBoard()[index], this.getBoard()[index + 2])) {
                return this.getBoard()[index];
            }
        }

        // Vertical
        for (int i = 0; i < 3; i++) {
            if (!Objects.equals(this.getBoard()[i], EMPTY) && Objects.equals(this.getBoard()[i], this.getBoard()[i + 3]) && Objects.equals(this.getBoard()[i], this.getBoard()[i + 6])) {
                return this.getBoard()[i];
            }
        }

        // B-Slash
        if (!Objects.equals(this.getBoard()[0], EMPTY) && Objects.equals(this.getBoard()[0], this.getBoard()[4]) && Objects.equals(this.getBoard()[0], this.getBoard()[8])) {
            return this.getBoard()[0];
        }

        // F-Slash
        if (!Objects.equals(this.getBoard()[2], EMPTY) && Objects.equals(this.getBoard()[2], this.getBoard()[4]) && Objects.equals(this.getBoard()[2], this.getBoard()[6]))
            return this.getBoard()[2];

        // Default return
        return EMPTY;
    }

    private boolean checkForEarlyDraw() {
        for (final int move : this.getLegalMoves()) {
            final TicTacToeR copy = this.deepCopy();

            if (copy.play(move).state() == GameState.WIN || !copy.checkForEarlyDraw()) {
                return false;
            }
        }

        return true;
    }

    public TicTacToeR deepCopy() {
        return new TicTacToeR(this);
    }
}
