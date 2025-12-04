package org.toop.framework.gameFramework.model.player;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface MoveProvider<T extends TurnBasedGame<T>> {
    int getMove(T game);
}
