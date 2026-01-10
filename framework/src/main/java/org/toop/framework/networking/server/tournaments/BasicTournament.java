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

public class BasicTournament implements Tournament {
    private final int POINTS_FOR_WIN = 1;

    private Server server;
    private String gameType;
    private ArrayList<TournamentMatch> matchList = new ArrayList<>();

    private final HashMap<NettyClient, List<NettyClient>> matches = new HashMap<>();
    private final HashMap<NettyClient, Integer> score = new HashMap<>();
    private NettyClient[] clients;

    public BasicTournament(Server server) {
        this.server = server;
    }

    @Override
    public void init(NettyClient[] clients, Shuffler shuffler) throws IllegalArgumentException {
        if (clients.length <= 1) throw new IllegalArgumentException("Not enough clients to initialize a tournament");

        for (NettyClient client : clients) {
            int INIT_SCORE = 0;

            matches.putIfAbsent(client, new ArrayList<>());
            score.putIfAbsent(client, INIT_SCORE);
        }

        for (var match : matches.entrySet()) {
            for (var matchNoSelf : matches.keySet()) {
                if (match.getKey() == matchNoSelf) continue;

                match.getValue().addLast(matchNoSelf);
            }
        }

        for (var client : matches.entrySet()) {
            for (var opponent : client.getValue()) {
                matchList.addLast(new TournamentMatch(client.getKey(), opponent));
            }
        }

        try {
            shuffler.shuffle(matchList);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public void addScorePoints(NettyClient client) {
        score.put(client, score.get(client) + POINTS_FOR_WIN);
    }

    @Override
    public boolean run(String gameType) throws RuntimeException {
        if (matchList.isEmpty()) throw new RuntimeException("No matches to start a tournament with");

        if (server.gameTypes().stream().noneMatch(e -> e.equalsIgnoreCase(gameType))) return false;
        this.gameType = gameType;

        new Thread(() -> {
            for (var match : matchList) {
                final CompletableFuture<Void> finished = new CompletableFuture<>();

                AtomicReference<OnlineGame<TurnBasedGame>> game = new AtomicReference<>();
                new Thread(() -> game.set(server.startGame(gameType, finished, match.getLeft(), match.getRight()))).start(); // TODO can possibly create a race condition

                finished.join();
                switch (game.get().game().getWinner()) {
                    case 0 -> addScorePoints(match.getLeft());
                    case 1 -> addScorePoints(match.getRight());
                    default -> {}
                }

                match.getLeft().clearGame();
                match.getRight().clearGame();
            }

            server.endTournament(end(), gameType);
        }).start();

        return true;
    }

    @Override
    public HashMap<NettyClient, Integer> end() {

        for (var match : matchList) {
            match.getLeft().clearGame(); // TODO send msg to client
            match.getRight().clearGame(); // TODO send msg to client
        }

        gameType = null;
        matchList = null;
        matches.clear();

        HashMap<NettyClient, Integer> retScore = new HashMap<>(score);

        score.clear();

        clients = null;

        return retScore;
    }
}
