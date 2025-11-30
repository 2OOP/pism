package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GUIEvents;
import org.toop.framework.gameFramework.PlayResult;
import org.toop.framework.gameFramework.abstractClasses.TurnBasedGameR;
import org.toop.framework.gameFramework.GameState;
import org.toop.game.players.AbstractPlayer;

/**
 * Handles local turn-based game logic in its own thread.
 * <p>
 * Repeatedly gets the current player's move, applies it to the game,
 * updates the UI, and stops when the game ends or {@link #stop()} is called.
 */
public class LocalThreadBehaviour extends ThreadBehaviourBase implements Runnable {

    /** All players participating in the game. */
    private final AbstractPlayer[] players;

    /**
     * Creates a new behaviour for a local turn-based game.
     *
     * @param game    the game instance
     * @param players the list of players in turn order
     */
    public LocalThreadBehaviour(TurnBasedGameR game, AbstractPlayer[] players) {
        super(game);
        this.players = players;
    }

    /** Starts the game loop in a new thread. */
    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            new Thread(this).start();
        }
    }

    /** Stops the game loop after the current iteration. */
    @Override
    public void stop() {
        isRunning.set(false);
    }

    /**
     * Main game loop: gets the current player's move, applies it,
     * updates the UI, and handles end-of-game states.
     */
    @Override
    public void run() {
        while (isRunning.get()) {
            AbstractPlayer currentPlayer = getCurrentPlayer();
            int move = currentPlayer.getMove(game.clone());
            PlayResult result = game.play(move);
            new EventFlow().addPostEvent(GUIEvents.RefreshGameCanvas.class).postEvent();

            GameState state = result.state();
            switch (state) {
                case WIN, DRAW -> {
                    isRunning.set(false);
                    new EventFlow().addPostEvent(
                            GUIEvents.GameEnded.class,
                            state == GameState.WIN,
                            result.player()
                    ).postEvent();
                }
                case NORMAL, TURN_SKIPPED -> { /* continue normally */ }
                default -> {
                    logger.error("Unexpected state {}", state);
                    isRunning.set(false);
                    throw new RuntimeException("Unknown state: " + state);
                }
            }
        }
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current active player
     */
    @Override
    public AbstractPlayer getCurrentPlayer() {
        return players[game.getCurrentTurn()];
    }
}
