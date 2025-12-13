package org.toop.framework.networking.server;

public interface ServerUser {
    long id();
    String name();
    Game game();
    void addGame(Game game);
    void removeGame();
    void setName(String name);
    void sendMessage(String message);
}
