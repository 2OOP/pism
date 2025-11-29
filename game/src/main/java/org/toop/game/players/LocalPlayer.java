package org.toop.game.players;

import org.toop.framework.games.GameR;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LocalPlayer extends AbstractPlayer {
    // Future can be used with event system, IF unsubscribeAfterSuccess works...
    // private CompletableFuture<Integer> LastMove = new CompletableFuture<>();

    private CompletableFuture<Integer> LastMove;

    public LocalPlayer(String name) {
        super(name);
    }

    @Override
    public int getMove(GameR gameCopy) {
        LastMove = new CompletableFuture<>();
        int move = -1;
        try {
            move = LastMove.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO: Add proper logging.
            e.printStackTrace();
        }
        return move;
    }

    public void setMove(int move) {
        System.out.println("setting move: " + move);
        LastMove.complete(move);
    }

    /*public void register() {
        // Listening to PlayerAttemptedMove
        new EventFlow().listen(GUIEvents.PlayerAttemptedMove.class, event -> {
            if (!LastMove.isDone()) {
                LastMove.complete(event.move()); // complete the future
            }
        }, true); // auto-unsubscribe
    }

    // This blocks until the next move arrives
    public int take() throws ExecutionException, InterruptedException {
        int move = LastMove.get(); // blocking
        LastMove = new CompletableFuture<>(); // reset for next move
        return move;
    }*/
}
