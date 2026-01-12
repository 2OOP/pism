package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.MatchExecutor;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.IntegerScoreSystem;

import java.time.Duration;

public interface TournamentRunner {
    void run(MatchExecutor matchExecutor, MatchMaker matchMaker, IntegerScoreSystem scoreSystem,
             ResultBroadcaster<IntegerScoreSystem> broadcaster, Duration turnTime, String gameType);
}
