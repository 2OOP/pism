package org.toop.framework.gameFramework;

import org.toop.framework.gameFramework.interfaces.IAIMoveR;

/**
 * Abstract base class for AI implementations for games extending {@link GameR}.
 * <p>
 * Provides a common superclass for specific AI algorithms. Concrete subclasses
 * must implement the {@link #findBestMove(GameR, int)} method defined by
 * {@link IAIMoveR} to determine the best move given a game state and a search depth.
 * </p>
 *
 * @param <T> the specific type of game this AI can play, extending {@link GameR}
 */
public abstract class AIR<T extends GameR> implements IAIMoveR<T> {
    // Concrete AI implementations should override findBestMove(T game, int depth)
}
