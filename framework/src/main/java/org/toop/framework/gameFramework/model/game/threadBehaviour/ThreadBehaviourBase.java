package org.toop.framework.gameFramework.model.game.threadBehaviour;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for thread-based game behaviours.
 * <p>
 * Provides common functionality for managing game state and execution:
 * a running flag, a game reference, and a logger.
 * Subclasses implement the actual game-loop logic.
 */
public abstract class ThreadBehaviourBase<T extends TurnBasedGame<T>> implements GameThreadStrategy<T> {
    private final Player<T>[] players;

    /** Indicates whether the game loop or event processing is active. */
    protected final AtomicBoolean isRunning = new AtomicBoolean();

    /** The game instance controlled by this behaviour. */
    protected final T game;

    /** Logger for the subclass to report errors or debug info. */
    protected final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * Creates a new base behaviour for the specified game.
     *
     * @param game the turn-based game to control
     */
    public ThreadBehaviourBase(T game, Player<T>[] players) {
        this.game = game;
        this.players = players;
    }

    /**
     * Returns the player whose turn it currently is.
     *
     * @return the current active player
     */
    @Override
    public Player<T> getCurrentPlayer() {
        return players[game.getCurrentTurn()];
    }

    public int getCurrentPlayerIndex() {return game.getCurrentTurn();}
}
