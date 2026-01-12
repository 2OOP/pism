package org.toop.framework.networking.server.tournaments.scoresystems;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DrawCountScoreSystem implements IntegerScoreSystem {

    private final Map<NettyClient, Integer> scores = new ConcurrentHashMap<>();
    private final int INIT_SCORE = 0;
    private final int WIN_POINTS = 1;

    public DrawCountScoreSystem() {} // TODO let user decide store type

    @Override
    public String scoreName() {
        return "draws";
    }

    @Override
    public void addPlayer(NettyClient user) {
        scores.putIfAbsent(user, INIT_SCORE);
    }

    @Override
    public void result(TournamentMatch match, Integer result) {
        switch (result) {
            case 0, 1 -> {}
            case -1 -> {
                scores.merge(match.getClient0(), WIN_POINTS, Integer::sum);
                scores.merge(match.getClient1(), WIN_POINTS, Integer::sum);
            }
            default -> throw new IllegalArgumentException("Unknown result: " + result);
        }
    }

    @Override
    public Map<NettyClient, Integer> getScore() {
        return scores;
    }
}
