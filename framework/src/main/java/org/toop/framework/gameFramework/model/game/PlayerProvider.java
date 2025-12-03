package org.toop.framework.gameFramework.model.game;

import org.toop.framework.gameFramework.model.player.Player;

public interface PlayerProvider<T extends TurnBasedGame<T>> {
    Player<T> getPlayer(int index);
}
