package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.GameResultFuture;
import org.toop.framework.networking.server.MatchExecutor;
import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.tournaments.matchmakers.MatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.IntegerScoreSystem;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class AsyncTournamentRunner implements TournamentRunner {

    @Override
    public void run(
            MatchExecutor matchRunner,
            MatchMaker matchMaker,
            IntegerScoreSystem scoreSystem,
            ResultBroadcaster<IntegerScoreSystem> broadcaster,
            Duration turnTime,
            String gameType
    ) {

        ExecutorService matchExecutor =
                Executors.newFixedThreadPool(
                        Runtime.getRuntime().availableProcessors()
                );

        Queue<TournamentMatch> pendingMatches = new ConcurrentLinkedQueue<>();
        matchMaker.forEach(pendingMatches::add);

        Set<NettyClient> busyPlayers = ConcurrentHashMap.newKeySet();
        List<CompletableFuture<Void>> runningMatches = new CopyOnWriteArrayList<>();

        try {
            while (!pendingMatches.isEmpty() || !runningMatches.isEmpty()) {

                Iterator<TournamentMatch> it = pendingMatches.iterator();
                while (it.hasNext()) {
                    TournamentMatch match = it.next();

                    NettyClient a = match.getClient0();
                    NettyClient b = match.getClient1();

                    // TODO game != null doesn't work here, fix later
                    if (busyPlayers.contains(a) || busyPlayers.contains(b)) {
                        continue;
                    }

                    busyPlayers.add(a);
                    busyPlayers.add(b);
                    it.remove();

                    CompletableFuture<Void> f =
                            CompletableFuture.runAsync(() -> {
                                try {
                                    GameResultFuture game = matchRunner.submit(gameType, turnTime, a, b);
                                    scoreSystem.result(match, game.result().join());
                                } finally {
                                    a.clearGame();
                                    b.clearGame();
                                    busyPlayers.remove(a);
                                    busyPlayers.remove(b);
                                }
                            }, matchExecutor);

                    runningMatches.add(f);

                    f.whenComplete((_, _) -> runningMatches.remove(f));
                }

                Thread.sleep(10); // Safety
            }

            broadcaster.broadcast(scoreSystem);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            matchExecutor.shutdown();
        }
    }
}
