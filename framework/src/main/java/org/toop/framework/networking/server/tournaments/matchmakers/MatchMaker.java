package org.toop.framework.networking.server.tournaments.matchmakers;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;

import java.util.List;

public interface MatchMaker extends Iterable<TournamentMatch> {
    List<NettyClient> getPlayers();
}
