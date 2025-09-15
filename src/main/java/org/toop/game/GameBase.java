package org.toop.game;

public abstract class GameBase {
	protected Player[] players;
	public int currentPlayer;

	protected int size;
	public char[] grid;

	public GameBase(int size) {
		currentPlayer = 0;

		this.size = size;
		grid = new char[size * size];

		for (int i = 0; i < grid.length; i++) {
			grid[i] = ' ';
		}
	}

	public Player[] Players() {
		return players;
	}

	public Player CurrentPlayer() {
		return players[currentPlayer];
	}

	public int Size() {
		return size;
	}

	public char[] Grid() {
		return grid;
	}

	public abstract boolean validateMove(int index);
	public abstract State playMove(int index);
}
