package org.toop.framework.networking.server.tournaments.scoresystems;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;

public interface IntegerScoreSystem extends ScoreSystem<TournamentMatch, Integer, NettyClient> {}
