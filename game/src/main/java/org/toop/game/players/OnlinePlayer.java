package org.toop.game.players;

/**
 * Represents a player controlled remotely or over a network.
 * <p>
 * This class extends {@link AbstractPlayer} and can be used to implement game logic
 * where moves are provided by an external source (e.g., another user or a server).
 * Currently, this class is a placeholder and does not implement move logic.
 * </p>
 */
public class OnlinePlayer extends AbstractPlayer {

    /**
     * Constructs a new OnlinePlayer.
     * <p>
     * Currently, no additional initialization is performed. Subclasses or
     * future implementations should provide mechanisms to receive moves from
     * an external source.
     */
    public OnlinePlayer(String name) {
        super(name);
    }
}
