package org.toop.game.Connect4;

import org.toop.game.TurnBasedGame;
import org.toop.game.enumerators.GameState;
import org.toop.game.records.Move;

import java.util.ArrayList;

public class Connect4 extends TurnBasedGame {
    private int movesLeft;

    public Connect4() {
        super(6, 7, 2);
        movesLeft = board.length;
    }

    public Connect4(Connect4 other) {
        super(other);
        movesLeft = other.movesLeft;
    }

    @Override
    public Move[] getLegalMoves() {
        final ArrayList<Move> legalMoves = new ArrayList<>();
        final char currentValue = getCurrentValue();

        for (int i = 0; i < columnSize; i++) {
            if (board[i] == EMPTY) {
                legalMoves.add(new Move(i, currentValue));
            }
        }
        return legalMoves.toArray(new Move[0]);
    }

    @Override
    public GameState play(Move move) {
        assert move != null;
        assert move.position() >= 0 && move.position() < board.length;
        assert move.value() == getCurrentValue();

        int lowestEmptySpot = move.position();
        for (int i = 0; i < rowSize; i++) {
            int checkMovePosition = move.position() + columnSize * i;
            if (checkMovePosition < board.length) {
                if  (board[checkMovePosition] == EMPTY) {
                    lowestEmptySpot = checkMovePosition;
                }
            }
        }
        board[lowestEmptySpot] = move.value();
        movesLeft--;

        if (checkForWin()) {
            return GameState.WIN;
        }

        nextTurn();


        return GameState.NORMAL;
    }

    private boolean checkForWin() {
        char[][] boardGrid = makeBoardAGrid();

        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < columnSize; col++) {
                char cell = boardGrid[row][col];
                if (cell == ' ' || cell == 0) continue;

                if (col + 3 < columnSize &&
                        cell == boardGrid[row][col + 1] &&
                        cell == boardGrid[row][col + 2] &&
                        cell == boardGrid[row][col + 3]) {
                    return true;
                }

                if (row + 3 < rowSize &&
                        cell == boardGrid[row + 1][col] &&
                        cell == boardGrid[row + 2][col] &&
                        cell == boardGrid[row + 3][col]) {
                    return true;
                }

                if (row + 3 < rowSize && col + 3 < columnSize &&
                        cell == boardGrid[row + 1][col + 1] &&
                        cell == boardGrid[row + 2][col + 2] &&
                        cell == boardGrid[row + 3][col + 3]) {
                    return true;
                }

                if (row + 3 < rowSize && col - 3 >= 0 &&
                        cell == boardGrid[row + 1][col - 1] &&
                        cell == boardGrid[row + 2][col - 2] &&
                        cell == boardGrid[row + 3][col - 3]) {
                    return true;
                }
            }
        }
        return false;
    }

    public char[][] makeBoardAGrid() {
        char[][] boardGrid = new char[rowSize][columnSize];
        for (int i = 0; i < rowSize*columnSize; i++) {
            boardGrid[i / columnSize][i % columnSize] = board[i];     //boardGrid[y -> row] [x -> column]
        }
        return  boardGrid;
    }

    private char getCurrentValue() {
        return currentTurn == 0 ? 'X' : 'O';
    }
}


