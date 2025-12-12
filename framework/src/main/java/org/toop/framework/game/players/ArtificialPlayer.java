package org.toop.framework.game.players;

import org.toop.framework.gameFramework.model.player.*;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

/**
 * Represents a player controlled by an AI in a game.
 * <p>
 * This player uses an {@link AbstractAI} instance to determine its moves. The generic
 * parameter {@code T} specifies the type of {@link GameR} the AI can handle.
 * </p>
 *
 * @param <T> the specific type of game this AI player can play
 */
public class ArtificialPlayer<T extends TurnBasedGame<T>> extends AbstractPlayer<T> {

    /** The AI instance used to calculate moves. */
    private final AI<T> ai;

    /**
     * Constructs a new ArtificialPlayer using the specified AI.
     *
     * @param ai the AI instance that determines moves for this player
     */
    public ArtificialPlayer(AI<T> ai, String name) {
        super(name);
        this.ai = ai;
    }

    public ArtificialPlayer(ArtificialPlayer<T> other) {
        super(other);
        this.ai = other.ai.deepCopy();
    }

    /**
     * Determines the next move for this player using its AI.
     * <p>
     * This method overrides {@link AbstractPlayer#getMove(GameR)}. Because the AI is
     * typed to {@code T}, a runtime cast is required. It is the caller's
     * responsibility to ensure that {@code gameCopy} is of type {@code T}.
     * </p>
     *
     * @param gameCopy a copy of the current game state
     * @return the integer representing the chosen move
     * @throws ClassCastException if {@code gameCopy} is not of type {@code T}
     */
    public long getMove(T gameCopy) {
        return ai.getMove(gameCopy);
    }

    @Override
    public ArtificialPlayer<T> deepCopy() {
        return new ArtificialPlayer<T>(this);
    }
}
