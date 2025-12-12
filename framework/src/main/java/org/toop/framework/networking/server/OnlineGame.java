package org.toop.framework.networking.server;

public interface OnlineGame<T> {
    long id();
    T game();
    User[] users();
}
