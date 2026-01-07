package org.toop.framework.networking.server;

import org.toop.framework.networking.server.client.NettyClient;

public interface OnlineGame<T> {
    long id();
    T game();
    NettyClient[] users();
    void start();
}
