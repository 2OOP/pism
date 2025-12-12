package org.toop.framework.gameFramework.model.player;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface MoveProvider {
    long getMove(TurnBasedGame game);
}
