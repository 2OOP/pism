package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.GameResultFuture;
import org.toop.framework.networking.server.MatchExecutor;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.IntegerScoreSystem;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class BasicTournamentRunner implements TournamentRunner {
    @Override
    public void run(
            MatchExecutor matchExecutor,
            MatchMaker matchMaker,
            List<IntegerScoreSystem> scoreSystems,
            ResultBroadcaster<IntegerScoreSystem> broadcaster,
            Duration turnTime,
            String gameType
    ) {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        try {
            threadPool.execute(() -> {
                for (TournamentMatch match : matchMaker) {
                    // Play game and await the results
                    GameResultFuture game = matchExecutor.submit(gameType, turnTime, match.getClient0(), match.getClient1());
                    scoreSystems.forEach(e -> e.result(match, game.result().join()));

                    match.getClient0().clearGame();
                    match.getClient1().clearGame();
                }

                broadcaster.broadcast(scoreSystems);
            });
        } finally {
            threadPool.shutdown();
        }
    }
}
