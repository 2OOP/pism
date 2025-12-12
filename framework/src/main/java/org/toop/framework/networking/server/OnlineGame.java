package org.toop.framework.networking.server;

public interface OnlineGame {
    long id();
    GameDefinition game();
    User[] users();
}
