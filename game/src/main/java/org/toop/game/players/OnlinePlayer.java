package org.toop.game.players;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractPlayer;
import org.toop.framework.gameFramework.model.player.Player;

/**
 * Represents a player controlled remotely or over a network.
 * <p>
 * This class extends {@link AbstractPlayer} and can be used to implement game logic
 * where moves are provided by an external source (e.g., another user or a server).
 * Currently, this class is a placeholder and does not implement move logic.
 * </p>
 */
public class OnlinePlayer<T extends TurnBasedGame<T>> extends AbstractPlayer<T> {

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

    public OnlinePlayer(OnlinePlayer<T> other) {
        super(other);
    }

    @Override
    public Player<T> deepCopy() {
        return new OnlinePlayer<>(this);
    }
}
