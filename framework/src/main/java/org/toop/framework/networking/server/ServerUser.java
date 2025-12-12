package org.toop.framework.networking.server;

public interface ServerUser {
    long id();
    String name();
    Game[] games();
    void addGame(Game game);
    void removeGame(Game game);
    void setName(String name);
    void sendMessage(String message);
}
