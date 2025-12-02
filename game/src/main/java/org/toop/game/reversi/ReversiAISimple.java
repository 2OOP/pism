package org.toop.game.reversi;

import org.toop.game.AI;
import org.toop.game.records.Move;

import java.util.Arrays;

public class ReversiAISimple extends AI<Reversi> {

    @Override
    public Move findBestMove(Reversi game, int depth) {
        //IO.println("****START FIND BEST MOVE****");

        Move[] moves = game.getLegalMoves();


        //game.printBoard();
        //IO.println("Legal moves: " + Arrays.toString(moves));

        Move bestMove;
        Move bestMoveScore = moves[0];
        Move bestMoveOptions = moves[0];
        int bestScore = -1;
        int bestOptions = -1;
        for (Move move : moves){
            int numOpt = getNumberOfOptions(game, move);
            if (numOpt > bestOptions) {
                bestOptions = numOpt;
                bestMoveOptions = move;
            }
            int numSco = getScore(game, move);
            if (numSco > bestScore) {
                bestScore = numSco;
                bestMoveScore = move;
            }

            //IO.println("Move: " + move.position() + ". Options: " + numOpt + ". Score: " + numSco);
        }
        if (bestScore > bestOptions) {
            bestMove = bestMoveScore;
        }
        else{
            bestMove = bestMoveOptions;
        }
        return bestMove;
    }

    private int getNumberOfOptions(Reversi game, Move move){
        Reversi copy = new Reversi(game);
        copy.play(move);
        return copy.getLegalMoves().length;
    }

    private int getScore(Reversi game, Move move){
        return game.getFlipsForPotentialMove(move).length;
    }
}
