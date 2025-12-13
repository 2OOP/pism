package org.toop.framework.networking.server;

import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.utils.Pair;

public interface ServerUser {
    long id();
    String name();
    Game game();
    ServerPlayer serverPlayer();
    void addGame(Pair<Game, ServerPlayer> gamePair);
    void removeGame();
    void setName(String name);
    void sendMessage(String message);
}
