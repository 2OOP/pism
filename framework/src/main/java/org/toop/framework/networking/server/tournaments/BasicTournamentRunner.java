package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.ScoreSystem;

import java.util.concurrent.*;

public class BasicTournamentRunner implements TournamentRunner {

    public BasicTournamentRunner() {}

    @Override
    public void run(Server server, MatchMaker matchMaker, ScoreSystem scoreSystem, String gameType) {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        try {
            threadPool.execute(() -> {
                for (var match : matchMaker) {
                    // Play game and await the results
                    var game = server.startGame(gameType, match.getClient0(), match.getClient1());
                    int result = game.result().join();
                    // End

                    // Get result and calculate new score
                    switch (result) {
                        case 0 -> scoreSystem.addScore(match.getClient0());
                        case 1 -> scoreSystem.addScore(match.getClient1());
                        case -1 -> {} // Draw
                        default -> {}
                    }

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
