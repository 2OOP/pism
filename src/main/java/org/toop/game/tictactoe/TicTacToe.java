package org.toop.game.tictactoe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.game.*;
import org.toop.server.backend.tictactoe.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TicTacToe extends GameBase implements Runnable {

	protected static final Logger logger = LogManager.getLogger(TicTacToe.class);

	public Thread gameThread;
	public String gameId;
	public BlockingQueue<ParsedCommand> commandQueue = new LinkedBlockingQueue<>();
	public BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();

	public int movesLeft;

	public TicTacToe(String player1, String player2) {
		super(3, new Player(player1, 'X'), new Player(player2, 'O'));
		movesLeft = size * size;
	}

	/**
	 *
	 * Used for the server.
	 *
	 * @param player1
	 * @param player2
	 * @param gameId
	 */
	public TicTacToe(String player1, String player2, String gameId) {
		super(3, new Player(player1, 'X'), new Player(player2, 'O'));
		this.gameId = gameId;
		movesLeft = size * size;
	}


	public void addCommandToQueue(ParsedCommand command) {
		commandQueue.add(command);
	}

    private void takeFromCommandQueue() {
        try {
            ParsedCommand cmd = this.commandQueue.take();
        } catch (InterruptedException e) {
            logger.error("Taking from queue interrupted, in game with id: {}", this.gameId);
        }
    }

	private void addSendToQueue(String send) {
        try {
            sendQueue.put(send);
        } catch (InterruptedException e) {
            logger.error("Sending to queue interrupted, in game with id: {}", this.gameId);
        }
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
            try {
                ParsedCommand cmd = this.commandQueue.take();
				logger.info("Game {}, took command: {}", this.gameId, cmd.originalCommand);
				this.addSendToQueue("OK");
            } catch (InterruptedException e) {
				logger.error("Game {} has crashed.", this.gameId);
                throw new RuntimeException(e);
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

	/**
	 * This method copies the board, mainly for AI use.
	 */
	public TicTacToe copyBoard() {
		TicTacToe clone = new TicTacToe(players[0].name(), players[1].name());
		System.arraycopy(this.grid, 0, clone.grid, 0, this.grid.length);
		clone.movesLeft = this.movesLeft;
		clone.currentPlayer = this.currentPlayer;
		return clone;
	}
}
