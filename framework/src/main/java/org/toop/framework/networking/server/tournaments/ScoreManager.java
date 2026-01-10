package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.client.NettyClient;

import java.util.Map;

public interface ScoreManager {
    void addClient(NettyClient client);
    void addScore(NettyClient client);
    Map<NettyClient, Integer> getScore();

    default int getWinPointAmount() {
        return 1;
    }

    default int getInitScore() {
        return 0;
    }
}
