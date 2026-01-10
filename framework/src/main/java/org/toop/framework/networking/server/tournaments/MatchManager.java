package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.client.NettyClient;

import java.util.List;

public interface MatchManager {
    void addClient(NettyClient client);
    List<NettyClient> getClients();
    void createMatches();
    TournamentMatch next();
    void run(Server server, ScoreManager scoreManager, String gameType);
    void shuffle(Shuffler shuffler);
}
