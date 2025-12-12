package org.toop.framework.gameFramework.model.player;

import org.toop.framework.gameFramework.model.game.DeepCopyable;

public interface Player extends NameProvider, MoveProvider, DeepCopyable<Player> {}
