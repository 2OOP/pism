package org.toop.framework.networking.server;

import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.challenges.gamechallenge.GameChallenge;
import org.toop.framework.networking.server.challenges.gamechallenge.GameChallengeTimer;
import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.stores.ClientStore;
import org.toop.framework.networking.server.stores.TurnBasedGameStore;
import org.toop.framework.networking.server.stores.TurnBasedGameTypeStore;
import org.toop.framework.utils.ImmutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.time.Duration;

public class Server implements GameServer {

    final private TurnBasedGameTypeStore gameTypesStore;
    final private ClientStore<Long, NettyClient> clientStore;
    final private List<GameChallenge> gameChallenges = new CopyOnWriteArrayList<>();
    final private TurnBasedGameStore gameStore;

    final private Duration challengeDuration;
    final private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Server(
            Duration challengeDuration,
            TurnBasedGameTypeStore turnBasedGameTypeStore,
            ClientStore<Long, NettyClient> clientStore,
            TurnBasedGameStore gameStore

    ) {
        this.gameTypesStore = turnBasedGameTypeStore;
        this.challengeDuration = challengeDuration;
        this.clientStore = clientStore;
        this.gameStore = gameStore;

        scheduler.schedule(this::serverTask, 0, TimeUnit.MILLISECONDS);
    }

    private void serverTask() {
        checkChallenges();
        scheduler.schedule(this::serverTask, 500, TimeUnit.MILLISECONDS);
    }

    public void addUser(NettyClient client) {
        clientStore.add(client);
    }

    public void removeUser(NettyClient client) {
        clientStore.remove(client.id());
    }

    public List<String> gameTypes() {
        return new ArrayList<>(gameTypesStore.all().keySet());
    }

    public List<OnlineGame<TurnBasedGame>> ongoingGames() {
        return gameStore.all().stream().toList();
    }

    public NettyClient getUser(String username) {
        return clientStore.all().stream().filter(e -> e.name().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    public NettyClient getUser(long id) {
        return clientStore.get(id);
    }

    public void challengeUser(String fromUser, String toUser, String gameType) {

        NettyClient from = getUser(fromUser);
        if (from == null) {
            return;
        }

        if (!gameTypesStore.all().containsKey(gameType)) {
            from.send("ERR gametype not found \n");
            return;
        }

        NettyClient to = getUser(toUser);
        if (to == null) {
            from.send("ERR user not found \n");
            return;
        }

        var ch = new GameChallenge(from, to, gameType, new GameChallengeTimer(challengeDuration));

        to.send(
                "SVR GAME CHALLENGE {CHALLENGER: \"%s\", CHALLENGENUMBER: \"%s\", GAMETYPE: \"%s\"} \n"
                        .formatted(from.name(), ch.id(), gameType)
        );

        if (!isValidChallenge(ch)) {
            warnUserExpiredChallenge(from, ch.id());
            ch.forceExpire();
            return;
        }

        gameChallenges.addLast(ch);
    }

    private void warnUserExpiredChallenge(NettyClient client, long challengeId) {
        client.send("SVR GAME CHALLENGE CANCELLED {CHALLENGENUMBER: \"" + challengeId + "\"}" + "\n");
    }

    private boolean isValidChallenge(GameChallenge gameChallenge) {
        for (var user : gameChallenge.getUsers()) {
            if (clientStore.get(user.id()) == null) {
                return false;
            }

            if (user.game() != null) {
                return false;
            }

            if (gameChallenge.isExpired()) {
                return false;
            }
        }

        return true;
    }

    public void checkChallenges() {
        for (int i = gameChallenges.size() - 1; i >= 0; i--) {
            var challenge = gameChallenges.get(i);

            if (isValidChallenge(challenge)) continue;

            if (challenge.isExpired()) {
                if (!challenge.isChallengeAccepted()) Arrays.stream(challenge.getUsers())
                        .forEach(user -> warnUserExpiredChallenge(user, challenge.id()));

                gameChallenges.remove(i);
            }
        }
    }

    public void acceptChallenge(long challengeId) {
        for (var challenge : gameChallenges) {
            if (challenge.id() == challengeId) {
                startGame(challenge.acceptChallenge(), challenge.getUsers());
                break;
            }
        }
    }

    public List<GameChallenge> gameChallenges() {
        return gameChallenges;
    }

    public void startGame(String gameType, NettyClient... clients) {
        if (!gameTypesStore.all().containsKey(gameType)) return;

        try {
            ServerPlayer[] players = new ServerPlayer[clients.length];
            var game = new Game(gameTypesStore.create(gameType), clients);

            for (int i = 0; i < clients.length; i++) {
                players[i] = new ServerPlayer(clients[i]);
                clients[i].addGame(new ImmutablePair<>(game, players[i]));
            }
            System.out.println("Starting Game");

            game.game().init(players);
            gameStore.add(game);

            clients[0].send(String.format("SVR GAME MATCH {PLAYERTOMOVE: \"%s\", GAMETYPE: \"%s\", OPPONENT: \"%s\"}\n",
                    clients[0].name(),
                    gameType,
                    clients[1].name()));
            clients[1].send(String.format("SVR GAME MATCH {PLAYERTOMOVE: \"%s\", GAMETYPE: \"%s\", OPPONENT: \"%s\"}\n",
                    clients[0].name(),
                    gameType,
                    clients[0].name()));
            game.start();
        } catch (Exception ignored) {}
    }

    public List<NettyClient> onlineUsers() {
        return clientStore.all().stream().toList();
    }

    public void closeServer() {
        scheduler.shutdown();
        gameChallenges.clear();
    }
}
