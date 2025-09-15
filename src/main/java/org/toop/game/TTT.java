package org.toop.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;

public class TTT extends GameBase {
	public int moveCount;
    private static final Logger logger = LogManager.getLogger(Main.class);

	public TTT(String player1, String player2) {
		super(3); // 3x3 Grid
		players = new Player[2];
		players[0] = new Player(player1, 'X');
		players[1] = new Player(player2, 'O');

		moveCount = 0;
	}

	@Override
	public boolean validateMove(int index) {
		if (index < 0 || index > (size * size - 1)) {
			return false;
		}

		return grid[index] == ' ';
	}

	@Override
	public State playMove(int index) {
		if (!validateMove(index)) {
			return State.INVALID;
		}

		grid[index] = players[currentPlayer].Move();
		moveCount += 1;

		if (checkWin()) {
			return State.WIN;
		}

		if (moveCount >= grid.length) {
			return State.DRAW;
		}

		currentPlayer = (currentPlayer + 1) % players.length;
		return State.NORMAL;
	}

	public boolean checkWin() {
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

    public TTT copyBoard() {
        /**
         * This method copies the board, mainly for AI use.
         */
        TTT clone = new TTT(players[0].Name(), players[1].Name());
        System.arraycopy(this.grid, 0, clone.grid, 0, this.grid.length);
        clone.moveCount = this.moveCount;
        clone.currentPlayer = this.currentPlayer;
        return clone;
    }
}
