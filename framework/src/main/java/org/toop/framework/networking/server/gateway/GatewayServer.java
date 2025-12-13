package org.toop.framework.networking.server.gateway;

public interface GatewayServer {
    void start() throws Exception;
    void stop();
    int port();
}
