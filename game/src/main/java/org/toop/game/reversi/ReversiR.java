package org.toop.game.reversi;

import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.PlayResult;
import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.game.TurnBasedGame;
import org.toop.game.records.Move;
import org.toop.game.tictactoe.TicTacToeR;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public final class ReversiR extends TurnBasedGameR {
    private int movesTaken;
    private Set<Point> filledCells = new HashSet<>();
    private int[] mostRecentlyFlippedPieces;

    // TODO: Don't hardcore for two players :)
    public record Score(int player1Score, int player2Score) {}

    @Override
    public ReversiR clone() {
        return new ReversiR(this);
    }

	public ReversiR() {
		super(8, 8, 2);
        addStartPieces();
	}

    public ReversiR(ReversiR other) {
        super(other);
        this.movesTaken = other.movesTaken;
        this.filledCells = other.filledCells;
        this.mostRecentlyFlippedPieces = other.mostRecentlyFlippedPieces;
    }


    private void addStartPieces() {
        this.setBoardPosition(27, 1);
        this.setBoardPosition(28, 0);
        this.setBoardPosition(35, 0);
        this.setBoardPosition(36, 1);
        updateFilledCellsSet();
    }
    private void updateFilledCellsSet() {
        for (int i = 0; i < 64; i++) {
            if (this.getBoard()[i] != EMPTY) {
                filledCells.add(new Point(i % this.getColumnSize(), i / this.getRowSize()));
            }
        }
    }

	@Override
	public int[] getLegalMoves() {
        final ArrayList<Integer> legalMoves = new ArrayList<>();
        int[][] boardGrid = makeBoardAGrid();
        int currentPlayer = this.getCurrentTurn();
        Set<Point> adjCell = getAdjacentCells(boardGrid);
        System.out.println(adjCell);
        for (Point point : adjCell){
            int[] moves = getFlipsForPotentialMove(point,currentPlayer);
            System.out.println(Arrays.toString(moves));
            int score = moves.length;
            if (score > 0){
                legalMoves.add(point.x + point.y * this.getRowSize());
            }
        }
        return legalMoves.stream().mapToInt(Integer::intValue).toArray();
	}

    private Set<Point> getAdjacentCells(int[][] boardGrid) {
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

    public int[] getFlipsForPotentialMove(Point point, int currentPlayer) {
        final ArrayList<Integer> movesToFlip = new ArrayList<>();
        for (int deltaColumn = -1; deltaColumn <= 1; deltaColumn++) {               //for all directions
            for (int deltaRow = -1; deltaRow <= 1; deltaRow++) {
                if (deltaColumn == 0 && deltaRow == 0){
                    continue;
                }
                int[] moves = getFlipsInDirection(point,makeBoardAGrid(),currentPlayer,deltaColumn,deltaRow);
                if (moves != null) {                                                //getFlipsInDirection
                    Arrays.stream(moves).forEach(movesToFlip::add);
                }
            }
        }
        return movesToFlip.stream().mapToInt(Integer::intValue).toArray();
    }

    private int[] getFlipsInDirection(Point point, int[][] boardGrid, int currentPlayer, int dirX, int dirY) {
        int opponent = getOpponent(currentPlayer);
        final ArrayList<Integer> movesToFlip = new ArrayList<>();
        int x = point.x + dirX;
        int y = point.y + dirY;

        if (!isOnBoard(x, y) || boardGrid[y][x] != opponent) {                          //there must first be an opponents tile
            return null;
        }

        while (isOnBoard(x, y) && boardGrid[y][x] == opponent) {                        //count the opponents tiles in this direction

            movesToFlip.add(x+y*this.getRowSize());
            x += dirX;
            y += dirY;
        }
        if (isOnBoard(x, y) && boardGrid[y][x] == currentPlayer) {
            return movesToFlip.stream().mapToInt(Integer::intValue).toArray();                                    //only return the count if last tile is ours
        }
        return null;
    }

    private boolean isOnBoard(int x, int y) {
        return x >= 0 && x < this.getColumnSize() && y >= 0 && y < this.getRowSize();
    }

    private int[][] makeBoardAGrid() {
        int[][] boardGrid = new int[this.getRowSize()][this.getColumnSize()];
        for (int i = 0; i < 64; i++) {
            boardGrid[i / this.getRowSize()][i % this.getColumnSize()] = this.getBoard()[i];     //boardGrid[y -> row] [x -> column]
        }
        return boardGrid;
    }

    private boolean gameOver(){
        ReversiR gameCopy = clone();
        return gameCopy.getLegalMoves().length == 0 && gameCopy.skipTurn().getLegalMoves().length == 0;
    }

    @Override
    public PlayResult play(int move) {
        /*int[] legalMoves = getLegalMoves();
        boolean moveIsLegal = false;
        for (int legalMove : legalMoves) {                         //check if the move is legal
            if (move == legalMove) {
                moveIsLegal = true;
                break;
            }
        }
        if (!moveIsLegal) {
            return null;
        }

        int[] moves = sortMovesFromCenter(Arrays.stream(getFlipsForPotentialMove(new Point(move%this.getColumnSize(),move/this.getRowSize()), getCurrentTurn())).boxed().toArray(Integer[]::new),move);
        mostRecentlyFlippedPieces = moves;
        this.setBoard(move);                                        //place the move on the board
        for (int m : moves) {
            this.setBoard(m);                                       //flip the correct pieces on the board
        }
        filledCells.add(new Point(move % this.getRowSize(), move / this.getColumnSize()));
        nextTurn();
        if (getLegalMoves().length == 0) {                          //skip the players turn when there are no legal moves
            skipMyTurn();
            if (getLegalMoves().length > 0) {
                return new PlayResult(GameState.TURN_SKIPPED, getCurrentTurn());
            }
            else {                                                  //end the game when neither player has a legal move
                Score score = getScore();
                if (score.player1Score() == score.player2Score()) {
                    return new PlayResult(GameState.DRAW, EMPTY);
                }
                else {
                    return new PlayResult(GameState.WIN, getCurrentTurn());
                }
            }
        }
        return new PlayResult(GameState.NORMAL, EMPTY);*/

        // Check if move is legal
        if (!contains(getLegalMoves(), move)){
            // Next person wins
            return new PlayResult(GameState.WIN, (getCurrentTurn() + 1) % 2);
        }

        // Move is legal, proceed as normal
        int[] moves = sortMovesFromCenter(Arrays.stream(getFlipsForPotentialMove(new Point(move%this.getColumnSize(),move/this.getRowSize()), getCurrentTurn())).boxed().toArray(Integer[]::new),move);
        mostRecentlyFlippedPieces = moves;
        this.setBoard(move);                                        //place the move on the board
        for (int m : moves) {
            this.setBoard(m);                                       //flip the correct pieces on the board
        }
        filledCells.add(new Point(move % this.getRowSize(), move / this.getColumnSize()));

        nextTurn();

        // Check for forced turn skip
        if (getLegalMoves().length == 0){
            PlayResult result;
            // Check if next turn is also a force skip
            if (clone().skipTurn().getLegalMoves().length == 0){
                // Game over
                int winner = getWinner();
                result = new PlayResult(winner == EMPTY ? GameState.DRAW : GameState.WIN, winner);
            }else{
                // Turn skipped
                result = new PlayResult(GameState.TURN_SKIPPED, getCurrentTurn());
                skipTurn();
            }
            return result;
        }
        return new PlayResult(GameState.NORMAL, EMPTY);
    }

    private ReversiR skipTurn(){
        nextTurn();
        return this;
    }

    private int getOpponent(int currentPlayer){
        return (currentPlayer + 1)%2;
    }

    public int getWinner(){
        int player1Score = 0, player2Score = 0;
        for (int count = 0; count < this.getRowSize() * this.getColumnSize(); count++) {
            if (this.getBoard()[count] == 0) {
                player1Score += 1;
            }
            if (this.getBoard()[count] == 1) {
                player2Score += 1;
            }
        }
        return player1Score == player2Score? -1 : player1Score > player2Score ? 0 : 1;
    }
    private int[] sortMovesFromCenter(Integer[] moves, int center) {                 //sorts the pieces to be flipped for animation purposes
        int centerX = center%this.getColumnSize();
        int centerY = center/this.getRowSize();
        Arrays.sort(moves, (a, b) -> {
            int dxA = a%this.getColumnSize() - centerX;
            int dyA = a/this.getRowSize() - centerY;
            int dxB = b%this.getColumnSize() - centerX;
            int dyB = b/this.getRowSize() - centerY;

            int distA = dxA * dxA + dyA * dyA;
            int distB = dxB * dxB + dyB * dyB;

            return Integer.compare(distA, distB);
        });
        return Arrays.stream(moves).mapToInt(Integer::intValue).toArray();
    }
    public int[] getMostRecentlyFlippedPieces() {
        return mostRecentlyFlippedPieces;
    }
}