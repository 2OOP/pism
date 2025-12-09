package org.toop.framework.gameFramework.model.player;

import org.toop.framework.gameFramework.model.game.DeepCopyable;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface AI<T extends TurnBasedGame<T>> extends MoveProvider<T>, DeepCopyable<AI<T>> {
}
