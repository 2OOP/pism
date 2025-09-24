package org.toop.tictactoe;

import org.toop.game.GameBase;

// Todo: refactor
public class TicTacToeAI {
	/**
	 * This method tries to find the best move by seeing if it can set a winning move, if not, it
	 * will do a minimax.
	 */
	public int findBestMove(TicTacToe game) {
		int bestVal = -100; // set bestVal to something impossible
		int bestMove = 10; // set bestMove to something impossible

		int winningMove = -5;

		boolean empty = true;
		for (char cell : game.grid) {
			if (!(cell == GameBase.EMPTY)) {
				empty = false;
				break;
			}
		}

		if (empty) { // start in a random corner
			return switch ((int) (Math.random() * 4)) {
				case 1 -> 2;
				case 2 -> 6;
				case 3 -> 8;
				default -> 0;
			};
		}

		// simulate all possible moves on the field
		for (int i = 0; i < game.grid.length; i++) {

			if (game.validateMove(i)) { // check if the move is legal here
				TicTacToe copyGame = game.copyBoard(); // make a copy of the game
				GameBase.State result = copyGame.play(i); // play a move on the copy board

				int thisMoveValue;

				if (result == GameBase.State.WIN) {
					return i; // just return right away if you can win on the next move
				}

				for (int index = 0; index < game.grid.length; index++) {
					if (game.validateMove(index)) {
						TicTacToe opponentCopy = copyGame.copyBoard();
						GameBase.State opponentResult = opponentCopy.play(index);
						if (opponentResult == GameBase.State.WIN) {
							winningMove = index;
						}
					}
				}

				thisMoveValue =
						doMinimax(copyGame, game.movesLeft, false); // else look at other moves
				if (thisMoveValue
						> bestVal) { // if better move than the current best, change the move
					bestVal = thisMoveValue;
					bestMove = i;
				}
			}
		}
		if (winningMove > -5) {
			return winningMove;
		}
		return bestMove; // return the best move when we've done everything
	}

	/**
	 * This method simulates all the possible future moves in the game through a copy in search of
	 * the best move.
	 */
	public int doMinimax(TicTacToe game, int depth, boolean maximizing) {
		boolean state = game.checkWin(); // check for a win (base case stuff)

		if (state) {
			if (maximizing) {
				// it's the maximizing players turn and someone has won. this is not good, so return
				// a negative value
				return -10 + depth;
			} else {
				// it is the turn of the AI and it has won! this is good for us, so return a
				// positive value above 0
				return 10 - depth;
			}
		} else {
			boolean empty = false;
			for (char cell :
					game.grid) { // else, look at draw conditions. we check per cell if it's empty
				// or not
				if (cell == GameBase.EMPTY) {
					empty = true; // if a thing is empty, set to true
					break; // break the loop
				}
			}
			if (!empty
					|| depth == 0) { // if the grid is full or the depth is 0 (both meaning game is
				// over) return 0 for draw
				return 0;
			}
		}

		int bestVal; // set the value to the highest possible
		if (maximizing) { // it's the maximizing players turn, the AI
			bestVal = -100;
			for (int i = 0; i < game.grid.length; i++) { // loop through the grid
				if (game.validateMove(i)) {
					TicTacToe copyGame = game.copyBoard();
					copyGame.play(i); // play the move on a copy board
					int value =
							doMinimax(copyGame, depth - 1, false); // keep going with the minimax
					bestVal =
							Math.max(
									bestVal,
									value); // select the best value for the maximizing player (the
					// AI)
				}
			}
		} else { // it's the minimizing players turn, the player
			bestVal = 100;
			for (int i = 0; i < game.grid.length; i++) { // loop through the grid
				if (game.validateMove(i)) {
					TicTacToe copyGame = game.copyBoard();
					copyGame.play(i); // play the move on a copy board
					int value = doMinimax(copyGame, depth - 1, true); // keep miniMaxing
					bestVal =
							Math.min(
									bestVal,
									value); // select the lowest score for the minimizing player,
					// they want to make it hard for us
				}
			}
		}
		return bestVal;
	}
}