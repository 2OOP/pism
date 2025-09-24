package org.toop.games;

// Todo: refactor
public abstract class GameBase {
	public enum State {
		INVALID,

		NORMAL,
		DRAW,
		WIN,
	}

	public static char EMPTY = '-';

	protected int size;
	public char[] grid;

	protected Player[] players;
	public int currentPlayer;

	public GameBase(int size, Player player1, Player player2) {
		this.size = size;
		grid = new char[size * size];

		for (int i = 0; i < grid.length; i++) {
			grid[i] = EMPTY;
		}

		players = new Player[2];
		players[0] = player1;
		players[1] = player2;

		currentPlayer = 0;
	}

	public boolean isInside(int index) {
		return index >= 0 && index < size * size;
	}

	public int getSize() {
		return size;
	}

	public char[] getGrid() {
		return grid;
	}

	public Player[] getPlayers() {
		return players;
	}

	public Player getCurrentPlayer() {
		return players[currentPlayer];
	}

	public abstract State play(int index);
}