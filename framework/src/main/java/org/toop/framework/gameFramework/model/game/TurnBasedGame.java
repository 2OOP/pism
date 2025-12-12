package org.toop.framework.gameFramework.model.game;

import org.toop.framework.gameFramework.model.player.Player;

public interface TurnBasedGame extends Playable, PlayerProvider, BoardProvider, DeepCopyable<TurnBasedGame> {
    void init(Player[] players);
    int getCurrentTurn();
    int getPlayerCount();
    int getWinner();
}
