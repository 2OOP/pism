package org.toop.framework.gameFramework.model.game.threadBehaviour;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

/**
 * Strategy interface for controlling game thread behavior.
 * <p>
 * Defines how a game's execution is started, stopped, and which player is active.
 */
public interface ThreadBehaviour<T extends TurnBasedGame<T>> extends Controllable {
}
