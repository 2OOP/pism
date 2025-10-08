package org.toop.game.reversi;

import org.toop.game.Game;
import org.toop.game.TurnBasedGame;
import org.toop.game.tictactoe.TicTacToe;

public final class Reversi extends TurnBasedGame {
    private int movesTaken;
    public static final char FIRST_MOVE = 'B';


	public Reversi() {
		super(8, 8, 2);
        addStartPieces();
	}

    public Reversi(Reversi other) {
        super(other);
        this.movesTaken = other.movesTaken;
    }


    private void addStartPieces() {
        board[27] = 'W';
        board[28] = 'B';
        board[35] = 'B';
        board[36] = 'W';

    }

	@Override
	public Move[] getLegalMoves() {
        char[][] boardGrid = makeBoardAGrid();
		if(currentTurn == 1){

        }
        return new Move[0];
	}

    public char[][] makeBoardAGrid() {
        char[][] boardGrid = new char[8][8];
        for (int i = 0; i < 64; i++) {
            boardGrid[i / 8][i % 8] = board[i];
        }
        return  boardGrid;
    }
    @Override
    public State play(Move move) {
        return null;
    }

    public char getCurrentPlayer() {
        if (currentTurn == 0){
            return 'B';
        }
        else {
            return 'W';
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