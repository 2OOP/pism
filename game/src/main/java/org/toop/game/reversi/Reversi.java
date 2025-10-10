package org.toop.game.reversi;

import org.toop.game.Game;
import org.toop.game.TurnBasedGame;
import org.toop.game.tictactoe.TicTacToe;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public final class Reversi extends TurnBasedGame {
    private int movesTaken;
    public static final char FIRST_MOVE = 'B';
    private Set<Point> filledCells = new HashSet<>();

	public Reversi() {
		super(8, 8, 2);
        addStartPieces();
	}

    public Reversi(Reversi other) {
        super(other);
        this.movesTaken = other.movesTaken;
        this.filledCells = other.filledCells;
    }


    private void addStartPieces() {
        board[27] = 'W';
        board[28] = 'B';
        board[35] = 'B';
        board[36] = 'W';
        updateFilledCellsSet();
    }
    private void updateFilledCellsSet() {
        for (int i = 0; i < 64; i++) {
            if (board[i] == 'W' || board[i] == 'B') {
                filledCells.add(new Point(i / 8, i % 8));
            }
        }
    }

	@Override
	public Move[] getLegalMoves() {
        final ArrayList<Move> legalMoves = new ArrayList<>();
        char[][] boardGrid = makeBoardAGrid();
        char currentPlayer = (currentTurn==0) ? 'B' : 'W';
        Set<Point> adjCell = getAdjacentCells(boardGrid);

        for (Point point : adjCell){
            Move[] moves = getFlipsForPotentialMove(point,boardGrid,currentPlayer);
            int score = moves.length;
            if (score > 0){
                legalMoves.add(new Move(point.x + point.y * 8, currentPlayer));
            }
        }
        return legalMoves.toArray(new Move[0]);
	}

    private Set<Point> getAdjacentCells(char[][] boardGrid) {
        Set<Point> possibleCells = new HashSet<>();
        for (Point point : filledCells) {                                           //for every filled cell
            for (int deltaColumn = -1; deltaColumn <= 1; deltaColumn++){            //check adjacent cells
                for (int deltaRow = -1; deltaRow <= 1; deltaRow++){                 //orthogonally and diagonally
                    int newX =  point.x + deltaColumn, newY =  point.y + deltaRow;
                    if (deltaColumn == 0 && deltaRow == 0                           //continue if out of bounds
                    || !isOnBoard(newX, newY)) {
                        continue;
                    }
                    if (boardGrid[newY][newX] == Game.EMPTY) {                      //check if the cell is empty
                        possibleCells.add(new Point(newX, newY));                   //and then add it to the set of possible moves
                    }
                }
            }
        }
        return possibleCells;
    }

    public Move[] getFlipsForPotentialMove(Point point, char[][] boardGrid, char currentPlayer) {
        final ArrayList<Move> movesToFlip = new ArrayList<>();
        for (int deltaColumn = -1; deltaColumn <= 1; deltaColumn++) {
            for (int deltaRow = -1; deltaRow <= 1; deltaRow++) {
                if (deltaColumn == 0 && deltaRow == 0){
                    continue;
                }
                Move[] moves = getFlipsInDirection(point,boardGrid,currentPlayer,deltaColumn,deltaRow);
                if (moves != null) {
                    movesToFlip.addAll(Arrays.asList(moves));
                }
            }
        }
        return movesToFlip.toArray(new Move[0]);
    }

    private Move[] getFlipsInDirection(Point point, char[][] boardGrid, char currentPlayer, int dirX, int dirY) {
        char opponent = getOpponent(currentPlayer);
        final ArrayList<Move> movesToFlip = new ArrayList<>();
        int x = point.x + dirX;
        int y = point.y + dirY;

        if (!isOnBoard(x, y) || boardGrid[y][x] != opponent) {
            return null;
        }

        while (isOnBoard(x, y) && boardGrid[y][x] == opponent) {

            movesToFlip.add(new Move(x+y*8, currentPlayer));
            x += dirX;
            y += dirY;
        }
        if (isOnBoard(x, y) && boardGrid[y][x] == currentPlayer) {
            return movesToFlip.toArray(new Move[0]);
        }
        return null;
    }

    private boolean isOnBoard(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }

    public char[][] makeBoardAGrid() {
        char[][] boardGrid = new char[8][8];
        for (int i = 0; i < 64; i++) {
            boardGrid[i / 8][i % 8] = board[i];     //boardGrid[y / row] [x / column]
        }
        return  boardGrid;
    }
    @Override
    public State play(Move move) {
        Move[] legalMoves = getLegalMoves();
        boolean moveIsLegal = false;
        for (Move legalMove : legalMoves) {
            if (move.equals(legalMove)) {
                moveIsLegal = true;
                break;
            }
        }
        if (moveIsLegal) {
            Move[] moves = getFlipsForPotentialMove(new Point(move.position()%8,move.position()/8), makeBoardAGrid(), move.value());
            board[move.position()] = move.value();
            for (Move m : moves) {
                board[m.position()] = m.value();
            }
            updateFilledCellsSet();
            nextTurn();
            return State.NORMAL;
        }
        return null;
    }

    public void skipMyTurn(){
        nextTurn();
    }

    public char getCurrentPlayer() {
        if (currentTurn == 0){
            return 'B';
        }
        else {
            return 'W';
        }
    }

    private char getOpponent(char currentPlayer){
        if (currentPlayer == 'B') {
            return 'W';
        }
        else {
            return 'B';
        }
    }

    public Game.Score getScore(){
        int player1Score = 0, player2Score = 0;
        for (int count = 0; count < 63; count++) {
            if (board[count] == 'W') {
                player1Score += 1;
            }
            if (board[count] == 'B') {
                player2Score += 1;
            }
        }
        return new Game.Score(player1Score, player2Score);
    }

}