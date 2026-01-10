package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.client.NettyClient;

import java.util.Map;

public class BasicScoreManager implements ScoreManager {

    private final Map<NettyClient, Integer> scores;

    public BasicScoreManager(Map<NettyClient, Integer> store) {
        scores = store;
    }

    @Override
    public void addClient(NettyClient client) {
        scores.putIfAbsent(client, getInitScore());
    }

    @Override
    public void addScore(NettyClient client) {
        int clientScore = scores.get(client);
        scores.put(client, clientScore + getWinPointAmount());
    }

    @Override
    public Map<NettyClient, Integer> getScore() {
        return scores;
    }
}
