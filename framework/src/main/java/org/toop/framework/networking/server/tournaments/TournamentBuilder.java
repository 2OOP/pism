package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.client.NettyClient;

import java.util.List;

public class TournamentBuilder {
    public Server server;
    public ScoreManager scoreManager;
    public MatchManager matchManager;

    public TournamentBuilder() {}

    public Tournament create(
            Tournament tournament,
            List<NettyClient> clients,
            Server server,
            ScoreManager scoreManager,
            MatchManager matchManager,
            Shuffler shuffler
    ) {

        this.server = server;
        this.scoreManager = scoreManager;
        this.matchManager = matchManager;

        for (var client : clients) {
            matchManager.addClient(client);
            scoreManager.addClient(client);
        }

        matchManager.createMatches();
        matchManager.shuffle(shuffler);

        tournament.init(this);
        return tournament;
    }
}
