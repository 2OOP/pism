package org.toop.game.tictactoe;

import org.toop.game.*;
import org.toop.server.backend.tictactoe.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TicTacToe extends GameBase implements Runnable {
	public Thread gameThread;
	public BlockingQueue<ParsedCommand> commandQueue = new LinkedBlockingQueue<>();
	public BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();

	public int movesLeft;

	public TicTacToe(String player1, String player2) {
		super(3, new Player(player1, 'X'), new Player(player2, 'O'));
		movesLeft = size * size;
	}

	public void addCommandToQueue(ParsedCommand command) {
		commandQueue.add(command);
	}

	@Override
	public void run() {
		this.gameThread = new Thread(this::gameThread);
		this.gameThread.start();
	}

	private void gameThread() {
		while (true) {
//			  String command = getNewestCommand();
//			  command = this.parseCommand(command).toString();
//			  if (command == null) { continue; }

			if (commandQueue.poll() == null) {
				continue;
			}

			// TODO: Game use the commandQueue to get the commands.
		}

	}

	@Override
	public State play(int index) {
		if (!validateMove(index)) {
			return State.INVALID;
		}

		grid[index] = getCurrentPlayer().move();
		movesLeft--;

		if (checkWin()) {
			return State.WIN;
		}

		if (movesLeft <= 0) {
			return State.DRAW;
		}

		currentPlayer = (currentPlayer + 1) % players.length;
		return State.NORMAL;
	}

	public boolean validateMove(int index) {
		return movesLeft > 0 && isInside(index) && grid[index] == EMPTY;
	}

	public boolean checkWin() {
		// Horizontal
		for (int i = 0; i < 3; i++) {
			final int index = i * 3;

			if (grid[index] != EMPTY && grid[index] == grid[index + 1] && grid[index] == grid[index + 2]) {
				return true;
			}
		}

		// Vertical
		for (int i = 0; i < 3; i++) {
			int index = i;

			if (grid[index] != EMPTY && grid[index] == grid[index + 3] && grid[index] == grid[index + 6]) {
				return true;
			}
		}

		// B-Slash
		if (grid[0] != EMPTY && grid[0] == grid[4] && grid[0] == grid[8]) {
			return true;
		}

		// F-Slash
		if (grid[2] != EMPTY && grid[2] == grid[4] && grid[2] == grid[6]) {
			return true;
		}

		return false;
	}

	/**
	 * For AI use only.
	 */
	public void decrementMovesLeft() {
		movesLeft--;
	}

	public TicTacToe copyBoard() {
		/**
		 * This method copies the board, mainly for AI use.
		 */
		TicTacToe clone = new TicTacToe(players[0].name(), players[1].name());
		System.arraycopy(this.grid, 0, clone.grid, 0, this.grid.length);
		clone.movesLeft = this.movesLeft;
		clone.currentPlayer = this.currentPlayer;
		return clone;
	}
}
