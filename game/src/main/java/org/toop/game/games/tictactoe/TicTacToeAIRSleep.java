package org.toop.game.games.tictactoe;

import java.util.Random;

public class TicTacToeAIRSleep extends TicTacToeAIR {

    private int thinkTime;

    public TicTacToeAIRSleep(int depth, int thinkTime) {
        super(depth);
        this.thinkTime = thinkTime;
    }

    @Override
    public int getMove(TicTacToeR game) {
        int score = super.getMove(game);
        try {
            Random random = new Random();
            Thread.sleep(this.thinkTime * 1000L +  random.nextInt(1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }
}
