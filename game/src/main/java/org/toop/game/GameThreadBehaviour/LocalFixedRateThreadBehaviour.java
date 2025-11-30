package org.toop.game.GameThreadBehaviour;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.PlayResult;
import org.toop.framework.gameFramework.TurnBasedGameR;
import org.toop.framework.gui.GUIEvents;
import org.toop.game.players.AbstractPlayer;

public class LocalFixedRateThreadBehaviour extends ThreadBehaviourBase implements Runnable{
    private final AbstractPlayer[] players;

    public LocalFixedRateThreadBehaviour(TurnBasedGameR game, AbstractPlayer[] players) {
        super(game);
        this.players = players;
    }

    @Override
    public void start() {
        if (isRunning.compareAndSet(false, true)){
            new Thread(this).start();
        }
    }

    @Override
    public void stop() {
        isRunning.set(false);
    }

    @Override
    public void run() {
        final int UPS = 60; // updates per second
        final long UPDATE_INTERVAL = 1_000_000_000L / UPS; // in nanoseconds

        long nextUpdate = System.nanoTime();

        while (isRunning.get()) {

            long now = System.nanoTime();

            if (now >= nextUpdate) {
                nextUpdate += UPDATE_INTERVAL;

                // === Your game logic ===

                // Get current player
                AbstractPlayer currentPlayer = getCurrentPlayer();

                // Get a valid move
                int move = currentPlayer.getMove(game.clone());

                // Play move
                PlayResult result = game.play(move);

                // Update UI
                new EventFlow()
                        .addPostEvent(GUIEvents.UpdateGameCanvas.class)
                        .postEvent();

                // Check game end
                GameState state = result.state();
                switch(state) {
                    case WIN, DRAW -> {
                        isRunning.set(false);
                        System.out.println("Posted ONCE");
                        new EventFlow().addPostEvent(GUIEvents.GameFinished.class, state == GameState.WIN, result.winner()).postEvent();
                    }
                    case NORMAL, TURN_SKIPPED ->{}
                    default -> {
                        // Unknown game state, stop running and throw error (maybe push an error event?)
                        isRunning.set(false);
                        throw new RuntimeException("Unknown state: " + state);
                    }
                }
            } else {
                // Sleep a tiny bit so the CPU doesn't burn 100%
                try {
                    Thread.sleep(10); // 0.2 ms
                } catch (InterruptedException ignored) {}
            }
        }
    }


    @Override
    public AbstractPlayer getCurrentPlayer() {
        return players[game.getCurrentTurn()];
    }


}
