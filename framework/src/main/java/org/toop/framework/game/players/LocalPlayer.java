package org.toop.framework.game.players;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractPlayer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class LocalPlayer extends AbstractPlayer {
    // Future can be used with event system, IF unsubscribeAfterSuccess works...
    // private CompletableFuture<Integer> LastMove = new CompletableFuture<>();

    private CompletableFuture<Long> LastMove;

    public LocalPlayer(String name) {
        super(name);
    }

    public LocalPlayer(LocalPlayer other) {
        super(other);
    }

    @Override
    public long getMove(TurnBasedGame gameCopy) {
        return getValidMove(gameCopy);
    }

    public void setMove(long move) {
        LastMove.complete(move);
    }

    // TODO: helper function, would like to replace to get rid of this method
    public static boolean contains(int[] array, int value){
        for (int i : array) if (i == value) return true;
        return false;
    }

    private long getMove2(TurnBasedGame gameCopy) {
        LastMove = new CompletableFuture<>();
        long move = 0;
        try {
            move = LastMove.get();
            System.out.println(Long.toBinaryString(move));
        } catch (InterruptedException | ExecutionException e) {
            // TODO: Add proper logging.
            e.printStackTrace();
        }
        return move;
    }

    protected long getValidMove(TurnBasedGame gameCopy){
        // Get this player's valid moves
        long validMoves = gameCopy.getLegalMoves();
        // Make sure provided move is valid
        // TODO: Limit amount of retries?
        // TODO: Stop copying game so many times
        long move = getMove2(gameCopy.deepCopy());
        while ((validMoves & move) == 0) {
            System.out.println("Not a valid move, try again");
            move = getMove2(gameCopy.deepCopy());
        }
        return move;
    }

    @Override
    public LocalPlayer deepCopy() {
        return new LocalPlayer(this.getName());
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
