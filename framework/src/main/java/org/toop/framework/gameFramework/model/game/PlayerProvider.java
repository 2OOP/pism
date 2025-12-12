package org.toop.framework.gameFramework.model.game;

import org.toop.framework.gameFramework.model.player.Player;

public interface PlayerProvider {
    Player getPlayer(int index);
}
