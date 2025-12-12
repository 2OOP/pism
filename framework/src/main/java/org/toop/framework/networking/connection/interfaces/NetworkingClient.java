package org.toop.framework.networking.connection.interfaces;

import org.toop.framework.networking.connection.exceptions.CouldNotConnectException;

import java.net.InetSocketAddress;

public interface NetworkingClient {
    InetSocketAddress getAddress();
    void connect(long clientId, String host, int port) throws CouldNotConnectException;
    boolean isActive();
    void writeAndFlush(String msg);
    void closeConnection();
}
