package org.toop.framework.networking.interfaces;

public interface NetworkingClient {
    boolean connect(long clientId, String host, int port);
    boolean isActive();
    void writeAndFlush(String msg);
    void closeConnection();
}
