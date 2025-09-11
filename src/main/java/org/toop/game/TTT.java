package org.toop.game;

public class TTT extends GameBase {
	private int moveCount;

	public TTT(String player1, String player2) {
		super(9);
		players = new Player[2];
		players[0] = new Player(player1, 'X');
		players[1] = new Player(player2, 'O');

		moveCount = 0;
	}

	@Override
	public boolean ValidateMove(int index) {
		if (index < 0 || index > (size * size - 1)) {
			return false;
		}

		return grid[index] == ' ';
	}

	@Override
	public State PlayMove(int index) {
		if (!ValidateMove(index)) {
			return State.INVALID;
		}

		grid[index] = players[currentPlayer].Move();
		moveCount += 1;

		if (CheckWin()) {
			return State.WIN;
		}

		if (moveCount >= grid.length) {
			return State.DRAW;
		}

		currentPlayer = (currentPlayer + 1) % players.length;
		return State.NORMAL;
	}

	private boolean CheckWin() {
		// Horizontal
		for (int i = 0; i < 3; i++) {
			int index = i * 3;

			if (grid[index] == grid[index + 1] && grid[index] == grid[index + 2]) {
				return true;
			}
		}

		// Vertical
		for (int i = 0; i < 3; i++) {
			int index = i;

			if (grid[index] == grid[index + 3] && grid[index] == grid[index + 6]) {
				return true;
			}
		}

		// F-Slash
		if (grid[2] == grid[4] && grid[2] == grid[6]) {
			return true;
		}

		// B-Slash
		if (grid[0] == grid[4] && grid[0] == grid[8]) {
			return true;
		}

		return false;
	}
}
