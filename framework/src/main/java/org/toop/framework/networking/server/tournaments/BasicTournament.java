package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;

public class BasicTournament implements Tournament {
    private Server server;

    private ScoreManager scoreManager;
    private MatchManager matchManager;

    public BasicTournament() {}

    public void init(TournamentBuilder builder) {
        server = builder.server;
        scoreManager = builder.scoreManager;
        matchManager = builder.matchManager;
    }

    @Override
    public boolean run(String gameType) throws RuntimeException {
        if (server.gameTypes().stream().noneMatch(e -> e.equalsIgnoreCase(gameType))) return false;

        matchManager.run(server, scoreManager, gameType);

        return true;
    }

//    @Override
//    public HashMap<NettyClient, Integer> end() {
//
//        for (var match : matchList) {
//            match.getLeft().clearGame(); // TODO send msg to client
//            match.getRight().clearGame(); // TODO send msg to client
//        }
//
//        gameType = null;
//        matchList = null;
//        matches.clear();
//
//        HashMap<NettyClient, Integer> retScore = new HashMap<>(score);
//
//        score.clear();
//
//        clients = null;
//
//        return retScore;
//    }
}
