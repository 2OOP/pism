package org.toop.game.players;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gui.GUIEvents;
import org.toop.game.GameR;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalPlayer extends Player {
    // TODO: This is AI mess, gotta clean it up and make it pretty.
    private CompletableFuture<Integer> LastMove = new CompletableFuture<>();

    public LocalPlayer() {}

    @Override
    public int getMove(GameR gameCopy) {
        register();
        int move = -1;
        try {
            System.out.println("Try to take move");
            move = take();
        }catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
        return move;
    }


    public void register() {
        // Listening to PlayerAttemptedMove
        new EventFlow().listen(GUIEvents.PlayerAttemptedMove.class, event -> {
            System.out.println("Player attempted move " + event.toString());
            if (!LastMove.isDone()) {
                LastMove.complete(event.move()); // complete the future
            }
        }, true); // auto-unsubscribe
    }

    // This blocks until the next move arrives
    public int take() throws ExecutionException, InterruptedException {
        System.out.println("TRYING TO GET FUTURE");
        int move = LastMove.get(); // blocking
        System.out.println("GOT PAST BLOCKING");
        LastMove = new CompletableFuture<>(); // reset for next move
        return move;
    }
}
