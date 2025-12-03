package org.toop.framework.gameFramework.model.game;

public interface TurnBasedGame<T extends TurnBasedGame<T>> extends Playable, DeepCopyable<T> {
    int getCurrentTurn();
}
