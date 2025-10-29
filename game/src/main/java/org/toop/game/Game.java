package org.toop.game;

import org.toop.game.interfaces.Playable;

import java.util.Arrays;

public abstract class Game implements Playable {
    public record Move(int position, char value) {}

	public static final char EMPTY = (char)0;

    public final int rowSize;
    public final int columnSize;
    public final char[] board;

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
}
