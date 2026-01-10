package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

public class TournamentBuilder {
    public Server server;
    public ScoreSystem scoreSystem;
    public TournamentRunner tournamentRunner;
    public MatchMaker matchMaker;

    public TournamentBuilder(
            Server server,
            TournamentRunner tournamentRunner,
            MatchMaker matchMaker,
            ScoreSystem scoreSystem
    ) {
        this.server = server;
        this.tournamentRunner = tournamentRunner;
        this.matchMaker = matchMaker;
        this.scoreSystem = scoreSystem;
    }
}
