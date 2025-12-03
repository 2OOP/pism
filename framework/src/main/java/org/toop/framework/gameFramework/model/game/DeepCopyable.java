package org.toop.framework.gameFramework.model.game;

public interface DeepCopyable<T extends TurnBasedGame<T>> {
    T deepCopy();
}
