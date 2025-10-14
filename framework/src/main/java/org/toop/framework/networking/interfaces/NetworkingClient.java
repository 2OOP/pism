package org.toop.framework.networking.interfaces;

public interface NetworkingClient {
    boolean isActive();
    void writeAndFlush(String msg);
    void closeConnection();
}
