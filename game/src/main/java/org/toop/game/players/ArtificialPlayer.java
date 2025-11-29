package org.toop.game.players;

import org.toop.framework.games.AIR;
import org.toop.framework.games.GameR;

/**
 * Represents a player controlled by an AI in a game.
 * <p>
 * This player uses an {@link AIR} instance to determine its moves. The generic
 * parameter {@code T} specifies the type of {@link GameR} the AI can handle.
 * </p>
 *
 * @param <T> the specific type of game this AI player can play
 */
public class ArtificialPlayer<T extends GameR> extends AbstractPlayer {

    /** The AI instance used to calculate moves. */
    private final AIR<T> ai;

    /**
     * Constructs a new ArtificialPlayer using the specified AI.
     *
     * @param ai the AI instance that determines moves for this player
     */
    public ArtificialPlayer(AIR<T> ai, String name) {
        super(name);
        this.ai = ai;
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
    @Override
    public int getMove(GameR gameCopy) {
        return ai.findBestMove((T) gameCopy, 9); // TODO: Make depth configurable
    }
}
