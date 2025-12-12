package org.toop.framework.gameFramework.model.game;

public interface TurnBasedGame extends Playable, PlayerProvider, BoardProvider, DeepCopyable<TurnBasedGame> {
    int getCurrentTurn();
    int getPlayerCount();
    int getWinner();
}
