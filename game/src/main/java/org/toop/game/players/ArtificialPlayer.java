package org.toop.game.players;

import org.toop.framework.gameFramework.model.player.*;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

/**
 * Represents a player controlled by an AI.
 *
 * @param <T> the type of turn-based game
 */
public class ArtificialPlayer<T extends TurnBasedGame<T>> extends AbstractPlayer<T> {

    private final AI<T> ai;

    /**
     * Creates a new AI-controlled player.
     *
     * @param ai the AI controlling this player
     * @param name the player's name
     */
    public ArtificialPlayer(AI<T> ai, String name) {
        super(name);
        this.ai = ai;
    }

    /**
     * Creates a copy of another AI-controlled player.
     *
     * @param other the player to copy
     */
    public ArtificialPlayer(ArtificialPlayer<T> other) {
        super(other);
        this.ai = other.ai.deepCopy();
    }

    /**
     * Determines the player's move using the AI.
     *
     * @param gameCopy a copy of the current game
     * @return the move chosen by the AI
     */
    protected long determineMove(T gameCopy) {
        return ai.getMove(gameCopy);
    }

    /**
     * Creates a deep copy of this AI player.
     *
     * @return a copy of this player
     */
    @Override
    public ArtificialPlayer<T> deepCopy() {
        return new ArtificialPlayer<>(this);
    }
}
