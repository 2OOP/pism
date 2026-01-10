package org.toop.framework.networking.server.tournaments.scoresystems;

import org.toop.framework.networking.server.client.NettyClient;

import java.util.Map;

public interface ScoreSystem {
    void addScore(NettyClient client);
    Map<NettyClient, Integer> getScore();

    default int getWinPointAmount() {
        return 1;
    }

    default int getInitScore() {
        return 0;
    }
}
