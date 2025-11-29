package org.toop.game.reversi;

import org.toop.game.TurnBasedGame;
import org.toop.framework.games.GameState;
import org.toop.game.records.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public final class Reversi extends TurnBasedGame {
    private int movesTaken;
    private Set<Point> filledCells = new HashSet<>();
    private Move[] mostRecentlyFlippedPieces;

	public record Score(int player1Score, int player2Score) {}

	public Reversi() {
		super(8, 8, 2);
        addStartPieces();
	}

    public Reversi(Reversi other) {
        super(other);
        this.movesTaken = other.movesTaken;
        this.filledCells = other.filledCells;
        this.mostRecentlyFlippedPieces = other.mostRecentlyFlippedPieces;
    }


    private void addStartPieces() {
        this.setBoard(new Move(27, 'W'));
        this.setBoard(new Move(28, 'B'));
        this.setBoard(new Move(35, 'B'));
        this.setBoard(new Move(36, 'W'));
        updateFilledCellsSet();
    }
    private void updateFilledCellsSet() {
        for (int i = 0; i < 64; i++) {
            if (this.getBoard()[i] == 'W' || this.getBoard()[i] == 'B') {
                filledCells.add(new Point(i % this.getColumnSize(), i / this.getRowSize()));
            }
        }
    }

	@Override
	public Move[] getLegalMoves() {
        final ArrayList<Move> legalMoves = new ArrayList<>();
        char[][] boardGrid = makeBoardAGrid();
        char currentPlayer = (this.getCurrentTurn()==0) ? 'B' : 'W';
        Set<Point> adjCell = getAdjacentCells(boardGrid);
        for (Point point : adjCell){
            Move[] moves = getFlipsForPotentialMove(point,currentPlayer);
            int score = moves.length;
            if (score > 0){
                legalMoves.add(new Move(point.x + point.y * this.getRowSize(), currentPlayer));
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
                    if (boardGrid[newY][newX] == EMPTY) {                           //check if the cell is empty
                        possibleCells.add(new Point(newX, newY));                   //and then add it to the set of possible moves
                    }
                }
            }
        }
        return possibleCells;
    }

    public Move[] getFlipsForPotentialMove(Point point, char currentPlayer) {
        final ArrayList<Move> movesToFlip = new ArrayList<>();
        for (int deltaColumn = -1; deltaColumn <= 1; deltaColumn++) {               //for all directions
            for (int deltaRow = -1; deltaRow <= 1; deltaRow++) {
                if (deltaColumn == 0 && deltaRow == 0){
                    continue;
                }
                Move[] moves = getFlipsInDirection(point,makeBoardAGrid(),currentPlayer,deltaColumn,deltaRow);
                if (moves != null) {                                                //getFlipsInDirection
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

        if (!isOnBoard(x, y) || boardGrid[y][x] != opponent) {                          //there must first be an opponents tile
            return null;
        }

        while (isOnBoard(x, y) && boardGrid[y][x] == opponent) {                        //count the opponents tiles in this direction

            movesToFlip.add(new Move(x+y*this.getRowSize(), currentPlayer));
            x += dirX;
            y += dirY;
        }
        if (isOnBoard(x, y) && boardGrid[y][x] == currentPlayer) {
            return movesToFlip.toArray(new Move[0]);                                    //only return the count if last tile is ours
        }
        return null;
    }

    private boolean isOnBoard(int x, int y) {
        return x >= 0 && x < this.getColumnSize() && y >= 0 && y < this.getRowSize();
    }

    private char[][] makeBoardAGrid() {
        char[][] boardGrid = new char[this.getRowSize()][this.getColumnSize()];
        for (int i = 0; i < 64; i++) {
            boardGrid[i / this.getRowSize()][i % this.getColumnSize()] = this.getBoard()[i];     //boardGrid[y -> row] [x -> column]
        }
        return  boardGrid;
    }

    @Override
    public GameState play(Move move) {
        Move[] legalMoves = getLegalMoves();
        boolean moveIsLegal = false;
        for (Move legalMove : legalMoves) {                         //check if the move is legal
            if (move.equals(legalMove)) {
                moveIsLegal = true;
                break;
            }
        }
        if (!moveIsLegal) {
            return null;
        }

        Move[] moves = sortMovesFromCenter(getFlipsForPotentialMove(new Point(move.position()%this.getColumnSize(),move.position()/this.getRowSize()), move.value()),move);
        mostRecentlyFlippedPieces = moves;
        this.setBoard(move);                                        //place the move on the board
        for (Move m : moves) {
            this.setBoard(m);                                       //flip the correct pieces on the board
        }
        filledCells.add(new Point(move.position() % this.getRowSize(), move.position() / this.getColumnSize()));
        nextTurn();
        if (getLegalMoves().length == 0) {                          //skip the players turn when there are no legal moves
            skipMyTurn();
            if (getLegalMoves().length > 0) {
                return GameState.TURN_SKIPPED;
            }
            else {                                                  //end the game when neither player has a legal move
                Score score = getScore();
                if (score.player1Score() == score.player2Score()) {
                    return GameState.DRAW;
                }
                else {
                    return GameState.WIN;
                }
            }
        }
        return GameState.NORMAL;
    }

    private void skipMyTurn(){
        IO.println("TURN " + getCurrentPlayer() + "  SKIPPED");
        //TODO: notify user that a turn has been skipped
        nextTurn();
    }

    public char getCurrentPlayer() {
        if (this.getCurrentTurn() == 0){
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

    public Score getScore(){
        int player1Score = 0, player2Score = 0;
        for (int count = 0; count < this.getRowSize() * this.getColumnSize(); count++) {
            if (this.getBoard()[count] == 'B') {
                player1Score += 1;
            }
            if (this.getBoard()[count] == 'W') {
                player2Score += 1;
            }
        }
        return new Score(player1Score, player2Score);
    }
    private Move[] sortMovesFromCenter(Move[] moves, Move center) {                 //sorts the pieces to be flipped for animation purposes
        int centerX = center.position()%this.getColumnSize();
        int centerY = center.position()/this.getRowSize();
        Arrays.sort(moves, (a, b) -> {
            int dxA = a.position()%this.getColumnSize() - centerX;
            int dyA = a.position()/this.getRowSize() - centerY;
            int dxB = b.position()%this.getColumnSize() - centerX;
            int dyB = b.position()/this.getRowSize() - centerY;

            int distA = dxA * dxA + dyA * dyA;
            int distB = dxB * dxB + dyB * dyB;

            return Integer.compare(distA, distB);
        });
        return moves;
    }
    public Move[] getMostRecentlyFlippedPieces() {
        return mostRecentlyFlippedPieces;
    }
}