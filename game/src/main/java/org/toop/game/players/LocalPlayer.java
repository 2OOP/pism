package org.toop.game.players;

import org.toop.game.GameR;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalPlayer extends Player {
    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();

    public LocalPlayer() {}

    @Override
    public int getMove(GameR gameCopy) {
        try {
            return queue.take();
        }catch (InterruptedException e){
            return -1;
        }
    }

    public void enqueueMove(int move) {
        System.out.println(move);
        queue.offer(move);
    }
}
