package org.toop.game;

import java.util.Arrays;

public abstract class Game {
	public enum State {
		NORMAL, DRAW, WIN,
	}

	public record Move(int position, char value) {}

    public record Score(int player1Score, int player2Score) {}

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

	public abstract Move[] getLegalMoves();
	public abstract State play(Move move);
}