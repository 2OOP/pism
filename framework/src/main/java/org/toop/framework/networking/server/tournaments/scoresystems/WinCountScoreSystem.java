package org.toop.framework.networking.server.tournaments.scoresystems;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WinCountScoreSystem implements IntegerScoreSystem {

    private final Map<NettyClient, Integer> scores = new ConcurrentHashMap<>();
    private final int INIT_SCORE = 0;
    private final int WIN_POINTS = 1;

    public WinCountScoreSystem() {} // TODO let user decide store type

    @Override
    public String scoreName() {
        return "wins";
    }

    @Override
    public void addPlayer(NettyClient user) {
        scores.putIfAbsent(user, INIT_SCORE);
    }

    @Override
    public void result(TournamentMatch match, Integer result) {
        switch (result) {
            case 0 -> scores.merge(match.getClient0(), WIN_POINTS, Integer::sum);
            case 1 -> scores.merge(match.getClient1(), WIN_POINTS, Integer::sum);
            case -1 -> {} // Draw
            default -> throw new IllegalArgumentException("Unknown result: " + result);
        }
    }

    @Override
    public Map<NettyClient, Integer> getScore() {
        return scores;
    }
}
