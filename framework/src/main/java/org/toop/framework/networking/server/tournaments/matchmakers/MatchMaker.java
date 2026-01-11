package org.toop.framework.networking.server.tournaments.matchmakers;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.TournamentMatch;
import org.toop.framework.networking.server.tournaments.shufflers.Shuffler;

import java.util.List;

public interface MatchMaker extends Iterable<TournamentMatch> {
    List<NettyClient> getPlayers();
    void shuffle(Shuffler shuffler);
}
