package org.toop.game;

import java.util.Arrays;

public abstract class Game {
	public enum State {
		NORMAL, LOSE, DRAW, WIN,
	}

	public record Move(int position, char value) {}

	public static final char EMPTY = (char)0;

	protected final int rowSize;
	protected final int columnSize;
	protected final char[] board;

	protected final Player[] players;
	protected int currentPlayer;

	protected Game(int rowSize, int columnSize, Player... players) {
		assert rowSize > 0 && columnSize > 0;
		assert players.length >= 1;

		this.rowSize = rowSize;
		this.columnSize = columnSize;

		board = new char[rowSize * columnSize];
		Arrays.fill(board, EMPTY);

		this.players = players;
		currentPlayer = 0;
	}

	protected Game(Game other) {
		rowSize = other.rowSize;
		columnSize = other.columnSize;
		board = Arrays.copyOf(other.board, other.board.length);

		players = Arrays.copyOf(other.players, other.players.length);
		currentPlayer = other.currentPlayer;
	}

	public int getRowSize() { return rowSize; }
	public int getColumnSize() { return columnSize; }
	public char[] getBoard() { return board; }

	public Player[] getPlayers() { return players; }
	public Player getCurrentPlayer() { return players[currentPlayer]; }

	protected void nextPlayer() {
		currentPlayer = (currentPlayer + 1) % players.length;
	}

	public abstract Move[] getLegalMoves();
	public abstract State play(Move move);
}