package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

public class BasicTournament implements Tournament {
    private final Server server;

    private final ScoreSystem scoreSystem;
    private final TournamentRunner tournamentRunner;
    private final MatchMaker matchMaker;

    public BasicTournament(TournamentBuilder builder) {
        server = builder.server;
        scoreSystem = builder.scoreSystem;
        tournamentRunner = builder.tournamentRunner;
        matchMaker = builder.matchMaker;
    }

    @Override
    public boolean run(String gameType) throws RuntimeException {
        if (server.gameTypes().stream().noneMatch(e -> e.equalsIgnoreCase(gameType))) return false;

        tournamentRunner.run(server, matchMaker, scoreSystem, gameType);

        return true;
    }
}
