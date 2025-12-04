package org.toop.game.gameThreads;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.game.threadBehaviour.AbstractThreadBehaviour;
import org.toop.framework.gameFramework.view.GUIEvents;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

/**
 * Handles local turn-based game logic at a fixed update rate.
 * <p>
 * Runs a separate thread that executes game turns at a fixed frequency (default 60 updates/sec),
 * applying player moves, updating the game state, and dispatching UI events.
 */
public class LocalFixedRateThreadBehaviour<T extends TurnBasedGame<T>> extends AbstractThreadBehaviour<T> implements Runnable {


    /**
     * Creates a fixed-rate behaviour for a local turn-based game.
     *
     * @param game    the game instance
     */
    public LocalFixedRateThreadBehaviour(T game) {
        super(game);
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
        final int UPS = 1;
        final long UPDATE_INTERVAL = 1_000_000_000L / UPS;
        long nextUpdate = System.nanoTime();

        while (isRunning.get()) {
            long now = System.nanoTime();
            if (now >= nextUpdate) {
                nextUpdate += UPDATE_INTERVAL;

                Player<T> currentPlayer = game.getPlayer(game.getCurrentTurn());
                int move = currentPlayer.getMove(game.deepCopy());
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
}
