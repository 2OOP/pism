package org.toop.framework.game.gameThreads;

import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.PlayResult;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.game.threadBehaviour.AbstractThreadBehaviour;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.gameFramework.view.GUIEvents;
import org.toop.framework.utils.ImmutablePair;
import org.toop.framework.utils.Pair;

import java.util.function.Consumer;

import static org.toop.framework.gameFramework.GameState.TURN_SKIPPED;
import static org.toop.framework.gameFramework.GameState.WIN;

public class ServerThreadBehaviour extends AbstractThreadBehaviour implements Runnable {
    private final Consumer<ImmutablePair<String, Integer>> onPlayerMove;
    private final Consumer<Pair<GameState, Integer>> onGameEnd;
    /**
     * Creates a new base behaviour for the specified game.
     *
     * @param game the turn-based game to control
     */
    public ServerThreadBehaviour(TurnBasedGame game, Consumer<ImmutablePair<String, Integer>> onPlayerMove, Consumer<Pair<GameState, Integer>> onGameEnd) {
        this.onPlayerMove = onPlayerMove;
        this.onGameEnd = onGameEnd;
        super(game);
    }

    private void notifyPlayerMove(ImmutablePair<String, Integer> pair) {
        if (onPlayerMove != null) {
            onPlayerMove.accept(pair);
        }
    }

    private void notifyGameEnd(ImmutablePair<GameState, Integer> pair) {
        if (onGameEnd != null) {
            onGameEnd.accept(pair);
        }
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
            notifyPlayerMove(new ImmutablePair<>(currentPlayer.getName(), Long.numberOfTrailingZeros(move)));

            switch (state) {
                case WIN, DRAW -> {
                    isRunning.set(false);
                    notifyGameEnd(new ImmutablePair<>(state, game.getWinner()));
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
