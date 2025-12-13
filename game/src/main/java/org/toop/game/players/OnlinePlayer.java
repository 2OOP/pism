package org.toop.game.players;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractPlayer;
import org.toop.framework.gameFramework.model.player.Player;

/**
 * Represents a player that participates online.
 *
 * @param <T> the type of turn-based game
 */
public class OnlinePlayer<T extends TurnBasedGame<T>> extends AbstractPlayer<T> {

    /**
     * Creates a new online player with the given name.
     *
     * @param name the name of the player
     */
    public OnlinePlayer(String name) {
        super(name);
    }

    /**
     * Creates a copy of another online player.
     *
     * @param other the player to copy
     */
    public OnlinePlayer(OnlinePlayer<T> other) {
        super(other);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is not supported for online players.
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    protected long determineMove(T gameCopy) {
        throw new UnsupportedOperationException("An online player does not support determining move");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Player<T> deepCopy() {
        return new OnlinePlayer<>(this);
    }
}
