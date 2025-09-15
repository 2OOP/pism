package org.toop.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;

/*
 * TTT = TIC TAC TOE FOR THE LESS EDUCATED POPULATION ON THIS CODE
 */

public class MinMaxTTT {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public int findBestMove(TTT game) {
        /**
         * This method tries to find the best move by seeing if it can set a winning move, if not, it will do a minimax.
         */
        int bestVal = -100; // set bestval to something impossible
        int bestMove = 10; // set bestmove to something impossible

        // simulate all possible moves on the field
        for (int i = 0; i < game.grid.length; i++) {
            if (game.ValidateMove(i)) {  // check if the move is legal here
                TTT copyGame = game.copyBoard(); // make a copy of the game
                State result = copyGame.PlayMove(i); // play a move on the copy board

                int thisMoveValue;

                if (result == State.WIN) {
                    return i; // just return right away if you can win on the next move
                }
                else {
                    thisMoveValue = doMinimax(copyGame, 8, false); // else look at other moves
                }
                if (thisMoveValue > bestVal) { // if better move than the current best, change the move
                    bestVal = thisMoveValue;
                    bestMove = i;
                }
            }
        }
        return bestMove; // return the best move when we've done everything
    }

    public int doMinimax(TTT game, int depth, boolean maximizing) {
        /**
         * This method simulates all the possible future moves in the game through a copy in search of the best move.
         */
        boolean state = game.CheckWin(); // check for a win (base case stuff)

        if (state) {
            if (maximizing) {
                // its the maximizing players turn and someone has won. this is not good, so return a negative value
                return -10 + depth;
            } else {
                // it is the turn of the ai and it has won! this is good for us, so return a positive value above 0
                return 10 - depth;
            }
        }

        else {
            boolean empty = false;
            for (char cell : game.grid) { // else, look at draw conditions. we check per cell if its empty or not
                if (cell == ' ') {
                    empty = true; // if a thing is empty, set to true
                    break; // break the loop
                }
            }
            if (empty || depth == 0) { // if the grid is empty or the depth is 0 (both meaning game is over) return 0 for draw
                return 0;
            }
        }

        if (maximizing) { // its the maximizing players turn, the AI
            int bestVal = -100; // set the value to lowest as possible
            for (int i = 0; i < game.grid.length; i++) { // loop through the grid
                if (game.ValidateMove(i)) {
                    TTT copyGame = game.copyBoard();
                    copyGame.PlayMove(i); // play the move on a copy board
                    int value = doMinimax(copyGame, depth - 1, false); // keep going with the minimax
                    bestVal = Math.max(bestVal, value); // select the best value for the maximizing player (the AI)
                }
            }
            return bestVal;
        }

        else { // it's the minimizing players turn, the player
            int bestVal = 100; // set the value to the highest possible
            for (int i = 0; i < game.grid.length; i++) { // loop through the grid
                if (game.ValidateMove(i)) {
                    TTT copyGame = game.copyBoard();
                    copyGame.PlayMove(i); // play the move on a copy board
                    int value = doMinimax(copyGame, depth - 1, true); // keep minimaxing
                    bestVal = Math.min(bestVal, value); // select the lowest score for the minimizing player, they want to make it hard for us
                }
            }
            return bestVal;
        }
    }
}
