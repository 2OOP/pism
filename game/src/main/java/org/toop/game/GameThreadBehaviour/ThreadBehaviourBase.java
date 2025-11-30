package org.toop.game.GameThreadBehaviour;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.gameFramework.abstractClasses.TurnBasedGameR;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for thread-based game behaviours.
 * <p>
 * Provides common functionality for managing game state and execution:
 * a running flag, a game reference, and a logger.
 * Subclasses implement the actual game-loop logic.
 */
public abstract class ThreadBehaviourBase implements GameThreadStrategy {

    /** Indicates whether the game loop or event processing is active. */
    protected final AtomicBoolean isRunning = new AtomicBoolean();

    /** The game instance controlled by this behaviour. */
    protected final TurnBasedGameR game;

    /** Logger for the subclass to report errors or debug info. */
    protected final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * Creates a new base behaviour for the specified game.
     *
     * @param game the turn-based game to control
     */
    public ThreadBehaviourBase(TurnBasedGameR game) {
        this.game = game;
    }
}
