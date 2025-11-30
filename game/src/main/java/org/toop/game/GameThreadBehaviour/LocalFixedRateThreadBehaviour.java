package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.PlayResult;
import org.toop.framework.gameFramework.abstractClasses.TurnBasedGameR;
import org.toop.framework.gameFramework.GUIEvents;
import org.toop.game.players.AbstractPlayer;

/**
 * Handles local turn-based game logic at a fixed update rate.
 * <p>
 * Runs a separate thread that executes game turns at a fixed frequency (default 60 updates/sec),
 * applying player moves, updating the game state, and dispatching UI events.
 */
public class LocalFixedRateThreadBehaviour extends ThreadBehaviourBase implements Runnable {

    /** All players participating in the game. */
    private final AbstractPlayer[] players;

    /**
     * Creates a fixed-rate behaviour for a local turn-based game.
     *
     * @param game    the game instance
     * @param players the list of players in turn order
     */
    public LocalFixedRateThreadBehaviour(TurnBasedGameR game, AbstractPlayer[] players) {
        super(game);
        this.players = players;
    }

    /** Starts the game loop thread if not already running. */
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
     * Main loop running at a fixed rate.
     * <p>
     * Fetches the current player's move, applies it to the game,
     * updates the UI, and handles game-ending states.
     */
    @Override
    public void run() {
        final int UPS = 60;
        final long UPDATE_INTERVAL = 1_000_000_000L / UPS;
        long nextUpdate = System.nanoTime();

        while (isRunning.get()) {
            long now = System.nanoTime();
            if (now >= nextUpdate) {
                nextUpdate += UPDATE_INTERVAL;

                AbstractPlayer currentPlayer = getCurrentPlayer();
                int move = currentPlayer.getMove(game.clone());
                PlayResult result = game.play(move);
                new EventFlow().addPostEvent(GUIEvents.RefreshGameCanvas.class).postEvent();

                GameState state = result.state();
                switch (state) {
                    case WIN, DRAW -> {
                        isRunning.set(false);
                        new EventFlow().addPostEvent(GUIEvents.GameEnded.class, state == GameState.WIN, result.player()).postEvent();
                    }
                    case NORMAL, TURN_SKIPPED -> { /* continue */ }
                    default -> {
                        logger.error("Unexpected state {}", state);
                        isRunning.set(false);
                        throw new RuntimeException("Unknown state: " + state);
                    }
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /** Returns the player whose turn it currently is. */
    @Override
    public AbstractPlayer getCurrentPlayer() {
        return players[game.getCurrentTurn()];
    }
}
