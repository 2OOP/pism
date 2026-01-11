package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.GameResultFuture;
import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

import java.util.concurrent.*;

public class BasicTournamentRunner implements TournamentRunner {
    @Override
    public void run(Server server, MatchMaker matchMaker, ScoreSystem scoreSystem, String gameType) {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        try {
            threadPool.execute(() -> {
                for (TournamentMatch match : matchMaker) {
                    // Play game and await the results
                    GameResultFuture game = server.startGame(gameType, match.getClient0(), match.getClient1());
                    scoreSystem.matchEndAwait(game);

                    match.getClient0().clearGame();
                    match.getClient1().clearGame();
                }

                server.endTournament(scoreSystem.getScore(), gameType);
            });
        } finally {
            threadPool.shutdown();
        }
    }
}
