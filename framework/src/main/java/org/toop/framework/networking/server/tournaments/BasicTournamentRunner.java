package org.toop.framework.networking.server.tournaments;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.OnlineGame;
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
                    final CompletableFuture<Void> finished = new CompletableFuture<>();

                    // Play game and await the results
                    OnlineGame<TurnBasedGame> game = server.startGame(gameType, finished, match.getClient0(), match.getClient1()); // TODO can possibly create a race condition
                    finished.join();
                    // End

                    // Get result and calculate new score
                    switch (game.game().getWinner()) {
                        case 0 -> scoreSystem.addScore(match.getClient0());
                        case 1 -> scoreSystem.addScore(match.getClient1());
                        default -> {
                        }
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
