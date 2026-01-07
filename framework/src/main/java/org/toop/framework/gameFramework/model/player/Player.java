package org.toop.framework.gameFramework.model.player;

import org.toop.framework.gameFramework.model.game.DeepCopyable;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface Player extends DeepCopyable<Player> {
    String getName();
    long getMove(TurnBasedGame game);
}
