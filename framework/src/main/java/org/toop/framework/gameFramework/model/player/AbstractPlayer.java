package org.toop.framework.gameFramework.model.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

/**
 * Abstract class representing a player in a game.
 * <p>
 * Players are entities that can make moves based on the current state of a game.
 * player types, such as human players or AI players.
 * </p>
 * <p>
 * Subclasses should override the {@link #getMove(GameR)} method to provide
 * specific move logic.
 * </p>
 */
public abstract class AbstractPlayer implements Player {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final String name;

    protected AbstractPlayer(String name) {
        System.out.println("Player " + name + " has been created");
        this.name = name;
    }

    protected AbstractPlayer(AbstractPlayer other) {
        this.name = other.name;
    }
    /**
     * Determines the next move based on the provided game state.
     * <p>
     * The default implementation throws an {@link UnsupportedOperationException},
     * indicating that concrete subclasses must override this method to provide
     * actual move logic.
     * </p>
     *
     * @param gameCopy a snapshot of the current game state
     * @return an integer representing the chosen move
     * @throws UnsupportedOperationException if the method is not overridden
     */
    public long getMove(TurnBasedGame gameCopy) {
        logger.error("Method getMove not implemented.");
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public final String getName(){
        return this.name;
    }
}
