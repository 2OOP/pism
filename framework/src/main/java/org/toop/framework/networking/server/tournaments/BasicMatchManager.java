package org.toop.framework.networking.server.tournaments;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.OnlineGame;
import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.client.NettyClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class BasicMatchManager implements MatchManager {

    private final HashMap<NettyClient, List<NettyClient>> matches = new HashMap<>(); // TODO store
    private final ArrayList<TournamentMatch> matchOrder = new ArrayList<>(); // TODO store
    private int matchIndex = 0;

    public BasicMatchManager() {}

    @Override
    public void addClient(NettyClient client) {
        matches.putIfAbsent(client, new ArrayList<>());
    }

    @Override
    public List<NettyClient> getClients() {
        return matches.keySet().stream().toList();
    }

    @Override
    public void createMatches() {
        for (var match : matches.entrySet()) {
            for (var matchNoSelf : matches.keySet()) {
                if (match.getKey() == matchNoSelf) continue;

                match.getValue().addLast(matchNoSelf);
            }
        }

        for (var client : matches.entrySet()) {
            for (var opponent : client.getValue()) {
                matchOrder.addLast(new TournamentMatch(client.getKey(), opponent));
            }
        }
    }

    @Override
    public TournamentMatch next() {
        matchIndex++;
        if (matchIndex >= matchOrder.size()) return null;

        return matchOrder.get(matchIndex);
    }

    @Override
    public void run(Server server, ScoreManager scoreManager, String gameType) {
        new Thread(() -> {
            for (var match : matchOrder) {
                final CompletableFuture<Void> finished = new CompletableFuture<>();

                AtomicReference<OnlineGame<TurnBasedGame>> game = new AtomicReference<>();
                new Thread(() -> game.set(server.startGame(gameType, finished, match.getLeft(), match.getRight()))).start(); // TODO can possibly create a race condition

                finished.join();
                switch (game.get().game().getWinner()) {
                    case 0 -> scoreManager.addScore(match.getLeft());
                    case 1 -> scoreManager.addScore(match.getRight());
                    default -> {}
                }

                match.getLeft().clearGame();
                match.getRight().clearGame();
            }

            server.endTournament(scoreManager.getScore(), gameType);
        }).start();
    }

    @Override
    public void shuffle(Shuffler shuffler) {
        shuffler.shuffle(matchOrder);
    }
}
