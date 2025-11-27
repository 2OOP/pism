package org.toop.game;

import org.toop.game.interfaces.IPlayable;
import org.toop.game.interfaces.IPlayableR;
import org.toop.game.records.Move;

import java.util.Arrays;

public abstract class GameR implements IPlayableR {

    public static final Integer EMPTY = null; // Constant

    private final int rowSize;
    private final int columnSize;
    private final Integer[] board;

    protected GameR(int rowSize, int columnSize) {
        assert rowSize > 0 && columnSize > 0;

        this.rowSize = rowSize;
        this.columnSize = columnSize;

        board = new Integer[rowSize * columnSize];
        Arrays.fill(board, EMPTY);
    }

    protected GameR(GameR other) {
        rowSize = other.rowSize;
        columnSize = other.columnSize;
        board = Arrays.copyOf(other.board, other.board.length);
    }

    public int getRowSize() {return this.rowSize;}

    public int getColumnSize() {return this.columnSize;}

    public Integer[] getBoard() {return this.board;}

    protected void setBoard(int position, int player){this.board[position] = player;}

}
