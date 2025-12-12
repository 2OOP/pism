package org.toop.framework.gameFramework.model.player;

import org.toop.framework.gameFramework.model.game.DeepCopyable;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public interface Player extends NameProvider, MoveProvider, DeepCopyable<Player> {
}
