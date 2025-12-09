package org.toop.framework.gameFramework.model.player;

import org.toop.framework.gameFramework.model.game.DeepCopyable;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface Player<T extends TurnBasedGame<T>> extends NameProvider, MoveProvider<T>, DeepCopyable<Player<T>> {
}
