package org.toop.framework.gameFramework.model.game;

public interface TurnBasedGame<T extends TurnBasedGame<T>> extends Playable, DeepCopyable<T>, PlayerProvider<T>, BoardProvider {
    int getCurrentTurn();
    int getPlayerCount();
    int getWinner();
}
