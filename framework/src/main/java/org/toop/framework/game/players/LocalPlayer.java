package org.toop.game.players;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractPlayer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Represents a local player who provides moves manually.
 *
 * @param <T> the type of turn-based game
 */
public class LocalPlayer extends AbstractPlayer {

    private CompletableFuture<Long> LastMove = new CompletableFuture<>();

    /**
     * Creates a new local player with the given name.
     *
     * @param name the player's name
     */
    public LocalPlayer(String name) {
        super(name);
    }

    /**
     * Creates a copy of another local player.
     *
     * @param other the player to copy
     */
    public LocalPlayer(LocalPlayer other) {
        super(other);
        this.LastMove = other.LastMove;
    }

    /**
     * Waits for and returns the player's next legal move.
     *
     * @param gameCopy a copy of the current game
     * @return the chosen move
     */
    @Override
    protected long determineMove(TurnBasedGame gameCopy) {
        long legalMoves = gameCopy.getLegalMoves();
        long move;

        do {
            move = getLastMove();
        } while ((legalMoves & move) == 0);

        return move;
    }

    /**
     * Sets the player's last move.
     *
     * @param move the move to set
     */
    public void setLastMove(long move) {
        LastMove.complete(move);
    }

    /**
     * Waits for the next move from the player.
     *
     * @return the chosen move or 0 if interrupted
     */
    private long getLastMove() {
        LastMove = new CompletableFuture<>(); // Reset the future
        try {
            return LastMove.get();
        } catch (ExecutionException | InterruptedException e) {
            return 0;
        }
    }

    /**
     * Creates a deep copy of this local player.
     *
     * @return a copy of this player
     */
    @Override
    public LocalPlayer deepCopy() {
        return new LocalPlayer(this);
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
