package org.toop.framework.networking.server.tournaments.scoresystems;

import org.toop.framework.networking.server.client.NettyClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicScoreSystem implements ScoreSystem {

    private final Map<NettyClient, Integer> scores = new ConcurrentHashMap<>();

    public BasicScoreSystem(List<NettyClient> store) {
        for (NettyClient c : store) {
            scores.putIfAbsent(c, getInitScore());
        }
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
