package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.client.NettyClient;

import java.util.HashMap;

public interface Tournament {
    void init(NettyClient[] clients, Shuffler shuffler);
    boolean start(String gameType);
    HashMap<NettyClient, Integer> end();
}
