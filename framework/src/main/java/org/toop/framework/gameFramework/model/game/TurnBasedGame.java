package org.toop.framework.gameFramework.model.game;

import org.toop.framework.gameFramework.model.player.Player;

public interface TurnBasedGame extends DeepCopyable<TurnBasedGame> {
    void init(Player[] players);
    long[] getBoard();
    int getCurrentTurn();
    int getPlayerCount();
    Player getPlayer(int index);
    int getWinner();
    long getLegalMoves();
    PlayResult play(long move);
}
