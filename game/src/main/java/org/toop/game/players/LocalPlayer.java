package org.toop.game.players;

import org.toop.framework.gameFramework.GameR;

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
        return getValidMove(gameCopy);
    }

    public void setMove(int move) {
        System.out.println("setting move: " + move);
        LastMove.complete(move);
    }

    // TODO: helper function, would like to replace to get rid of this method
    public static boolean contains(int[] array, int value){
        for (int i : array) if (i == value) return true;
        return false;
    }

    private int getMove2(GameR gameCopy) {
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

    protected int getValidMove(GameR gameCopy){
        // Get this player's valid moves
        int[] validMoves = gameCopy.getLegalMoves();
        // Make sure provided move is valid
        // TODO: Limit amount of retries?
        // TODO: Stop copying game so many times
        int move = getMove2(gameCopy.clone());
        while (!contains(validMoves, move)) {
            System.out.println("Not a valid move, try again");
            move = getMove2(gameCopy.clone());
        }
        return move;
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
