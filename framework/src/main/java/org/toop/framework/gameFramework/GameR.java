package org.toop.framework.gameFramework;

import org.toop.framework.gameFramework.interfaces.IPlayableR;

import java.util.Arrays;

/**
 * Abstract base class representing a general grid-based game.
 * <p>
 * Provides the basic structure for games with a two-dimensional board stored as a
 * one-dimensional array. Tracks the board state, row and column sizes, and provides
 * helper methods for accessing and modifying the board.
 * </p>
 * <p>
 * Concrete subclasses must implement the {@link #clone()} method and can extend this
 * class with specific game rules, winning conditions, and move validation logic.
 * </p>
 */
public abstract class GameR implements IPlayableR, Cloneable {

    /** Constant representing an empty position on the board. */
    public static final int EMPTY = -1;

    /** Number of rows in the game board. */
    private final int rowSize;

    /** Number of columns in the game board. */
    private final int columnSize;

    /** The game board stored as a one-dimensional array. */
    private final int[] board;

    /**
     * Constructs a new game board with the specified row and column size.
     *
     * @param rowSize    number of rows (> 0)
     * @param columnSize number of columns (> 0)
     * @throws AssertionError if rowSize or columnSize is not positive
     */
    protected GameR(int rowSize, int columnSize) {
        assert rowSize > 0 && columnSize > 0;

        this.rowSize = rowSize;
        this.columnSize = columnSize;

        board = new int[rowSize * columnSize];
        Arrays.fill(board, EMPTY);
    }

    /**
     * Copy constructor for creating a deep copy of another game instance.
     *
     * @param copy the game instance to copy
     */
    protected GameR(GameR copy) {
        this.rowSize = copy.rowSize;
        this.columnSize = copy.columnSize;
        this.board = copy.board.clone();
    }

    /**
     * Returns the number of rows in the board.
     *
     * @return number of rows
     */
    public int getRowSize() {
        return this.rowSize;
    }

    /**
     * Returns the number of columns in the board.
     *
     * @return number of columns
     */
    public int getColumnSize() {
        return this.columnSize;
    }

    /**
     * Returns a copy of the current board state.
     *
     * @return a cloned array representing the board
     */
    public int[] getBoard() {
        return this.board.clone();
    }

    /**
     * Sets the value of a specific position on the board.
     *
     * @param position the index in the board array
     * @param player   the value to set (e.g., player number)
     */
    protected void setBoardPosition(int position, int player) {
        this.board[position] = player;
    }

    /**
     * Creates and returns a deep copy of this game instance.
     * <p>
     * Subclasses must implement this method to ensure proper copying of any
     * additional fields beyond the base board structure.
     * </p>
     *
     * @return a cloned instance of this game
     */
    @Override
    public abstract GameR clone();
}
