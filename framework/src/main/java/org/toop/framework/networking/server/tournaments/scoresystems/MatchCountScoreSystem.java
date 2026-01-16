package org.toop.framework.networking.server.tournaments.scoresystems;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchCountScoreSystem implements IntegerScoreSystem {

    private final Map<NettyClient, Integer> scores = new ConcurrentHashMap<>();
    private final int INIT_SCORE = 0;
    private final int WIN_POINTS = 1;

    public MatchCountScoreSystem() {} // TODO let user decide store type

    @Override
    public String scoreName() {
        return "matches";
    }

    @Override
    public void addPlayer(NettyClient user) {
        scores.putIfAbsent(user, INIT_SCORE);
    }

    @Override
    public void result(TournamentMatch match, Integer result) {
        scores.merge(match.getClient0(), WIN_POINTS, Integer::sum);
        scores.merge(match.getClient1(), WIN_POINTS, Integer::sum);
    }

    @Override
    public Map<NettyClient, Integer> getScore() {
        return scores;
    }
}
