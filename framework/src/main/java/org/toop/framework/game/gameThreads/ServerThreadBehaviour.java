package org.toop.framework.game.gameThreads;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.game.threadBehaviour.AbstractThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.gameFramework.view.GUIEvents;

import java.util.function.Consumer;

public class ServerThreadBehaviour extends AbstractThreadBehaviour implements Runnable {
    private Consumer<Integer> onPlayerMove;
    /**
     * Creates a new base behaviour for the specified game.
     *
     * @param game the turn-based game to control
     */
    public ServerThreadBehaviour(TurnBasedGame game, Consumer<Integer> onPlayerMove) {
        super(game);
    }

    private void notifyPlayerMove(int player) {
        onPlayerMove.accept(player);
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

    @Override
    public void run() {
        while (isRunning.get()) {
            Player currentPlayer = game.getPlayer(game.getCurrentTurn());
            long move = currentPlayer.getMove(game.deepCopy());
            PlayResult result = game.play(move);

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
}
