package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

public interface TournamentRunner {
    void run(Server server, MatchMaker matchMaker, ScoreSystem scoreSystem, String gameType);
}
