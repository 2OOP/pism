package org.toop.framework.networking.server.tournaments.scoresystems;

import org.toop.framework.networking.server.GameResultFuture;
import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;

import java.util.Map;

public interface ScoreSystem {
    void matchEndAwait(GameResultFuture result);
    Map<NettyClient, Integer> getScore();

    default int getWinPointAmount() {
        return 1;
    }
    default int getInitScore() {
        return 0;
    }
}
