package org.toop.app.game;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalPlayer extends Player{
    private BlockingQueue<Integer> queue = new LinkedBlockingQueue<Integer>();

    public LocalPlayer() {}

    @Override
    public int getMove() {
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
