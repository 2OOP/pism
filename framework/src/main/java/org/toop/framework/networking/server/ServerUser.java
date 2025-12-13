package org.toop.framework.networking.server;

import org.toop.framework.game.players.ServerPlayer;

import java.util.Map;

public interface ServerUser {
    long id();
    String name();
    Game game();
    ServerPlayer serverPlayer();
    void addGame(Game game, ServerPlayer player);
    void removeGame();
    void setName(String name);
    void sendMessage(String message);
}
