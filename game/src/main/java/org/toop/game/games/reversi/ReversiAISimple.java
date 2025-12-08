package org.toop.game.games.reversi;

import org.toop.framework.gameFramework.model.player.AbstractAI;

import java.awt.*;

public class ReversiAISimple  extends AbstractAI<ReversiR> {


    private int getNumberOfOptions(ReversiR game, int move){
        ReversiR copy = game.deepCopy();
        copy.play(move);
        return copy.getLegalMoves().length;
    }

    private int getScore(ReversiR game, int move){
        return game.getFlipsForPotentialMove(new Point(move%game.getColumnSize(),move/game.getRowSize()),game.getCurrentTurn()).length;
    }

    @Override
    public int getMove(ReversiR game) {

        int[] moves = game.getLegalMoves();

        int bestMove;
        int bestMoveScore = moves[0];
        int bestMoveOptions = moves[0];
        int bestScore = -1;
        int bestOptions = -1;
        for (int move : moves){
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
            if (numSco == bestScore || numOpt == bestOptions) {
                if (Math.random() < 0.5) {
                    bestMoveOptions = move;
                    bestMoveScore = move;
                }
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
}
