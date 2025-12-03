package org.toop.framework.gameFramework.model.game;

import org.toop.framework.gameFramework.model.player.Player;

import java.util.Arrays;

public abstract class AbstractGame<T extends TurnBasedGame<T>> implements TurnBasedGame<T> {
    private final int playerCount;  // How many players are playing
    private final Player<T>[] players;
    private int turn = 0;           // What turn it is in the game

    /** Constant representing an empty position on the board. */
    public static final int EMPTY = -1;

    /** Number of rows in the game board. */
    private final int rowSize;

    /** Number of columns in the game board. */
    private final int columnSize;

    /** The game board stored as a one-dimensional array. */
    private final int[] board;



    protected AbstractGame(int rowSize, int columnSize, int playerCount, Player<T>[] players) {
        assert rowSize > 0 && columnSize > 0;

        this.rowSize = rowSize;
        this.columnSize = columnSize;

        this.players = players;

        board = new int[rowSize * columnSize];
        Arrays.fill(board, EMPTY);

        this.playerCount = playerCount;
    }

    protected AbstractGame(AbstractGame<T> other){
        this.rowSize = other.rowSize;
        this.columnSize = other.columnSize;
        this.board = other.board.clone();
        this.playerCount = other.playerCount;
        this.turn = other.turn;
        // TODO: Make this a deep copy, add deep copy interface to Player
        this.players = other.players;

    }

    public static boolean contains(int[] array, int value) {
        // O(n)
        for (int element : array){
            if (element == value) return true;
        }
        return false;
    }

    public Player<T> getPlayer(int index) {
        return players[index];
    }

    public int getPlayerCount(){return this.playerCount;}

    protected void nextTurn() {
        turn += 1;
    }

    public int getCurrentTurn() {
        return turn % playerCount;
    }

    protected void setBoard(int position) {
        setBoard(position, getCurrentTurn());
    }

    protected void setBoard(int position, int player) {
        this.board[position] = player;
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

}
