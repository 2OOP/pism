package org.toop.game;

import org.toop.game.interfaces.IPlayable;
import org.toop.game.records.Move;

import java.util.Arrays;

public abstract class Game implements IPlayable {

	public static final char EMPTY = (char)0; // Constant

    private final int rowSize;
    private final int columnSize;
    private final char[] board;

    protected Game(int rowSize, int columnSize) {
        assert rowSize > 0 && columnSize > 0;

        this.rowSize = rowSize;
        this.columnSize = columnSize;

        board = new char[rowSize * columnSize];
        Arrays.fill(board, EMPTY);
    }

    protected Game(Game other) {
        rowSize = other.rowSize;
        columnSize = other.columnSize;
        board = Arrays.copyOf(other.board, other.board.length);
    }

    public int getRowSize() {return this.rowSize;}

    public int getColumnSize() {return this.columnSize;}

    public char[] getBoard() {return this.board;}

    protected void setBoard(Move move){this.board[move.position()] = move.value();}

}
