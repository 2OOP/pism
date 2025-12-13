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

import java.util.*;
import java.util.concurrent.*;
import java.time.Duration;

public class Server implements GameServer<TurnBasedGame, NettyClient, Long> {

    final private TurnBasedGameTypeStore gameTypesStore;
    final private ClientStore<Long, NettyClient> clientStore;
    final private List<GameChallenge> gameChallenges = new CopyOnWriteArrayList<>();
    final private TurnBasedGameStore gameStore;

    final private ConcurrentHashMap<String, List<String>> subscriptions; // TODO move to own store / manager

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
        this.subscriptions = new ConcurrentHashMap<>();

        scheduler.schedule(this::serverTask, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addClient(NettyClient client) {
        clientStore.add(client);
    }

    @Override
    public void removeClient(NettyClient client) {
        clientStore.remove(client.id());
    }

    @Override
    public List<String> gameTypes() {
        return new ArrayList<>(gameTypesStore.all().keySet());
    }

    @Override
    public List<OnlineGame<TurnBasedGame>> ongoingGames() {
        return gameStore.all().stream().toList();
    }

    @Override
    public void challengeClient(String fromUser, String toUser, String gameType) {

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

    @Override
    public void acceptChallenge(Long challengeId) {
        for (var challenge : gameChallenges) {
            if (challenge.id() == challengeId) {
                startGame(challenge.acceptChallenge(), challenge.getUsers());
                break;
            }
        }
    }

    @Override
    public void subscribeClient(String clientName, String gameTypeKey) {

        if (!gameTypesStore.all().containsKey(gameTypeKey)) {
            return;
        }

        subscriptions.forEach((_, clientNames) -> clientNames.remove(clientName));
        subscriptions.computeIfAbsent(
                            gameTypeKey,
                        _ -> new ArrayList<>())
                .add(clientName);
    }

    @Override
    public void unsubscribeClient(String clientName) {
        subscriptions.forEach((_, clientNames) -> clientNames.remove(clientName));
    }

    @Override
    public void startGame(String gameType, NettyClient... clients) {
        IO.println("------------------------------------------");

        IO.println("USERS: " + clients.length + " " + Arrays.stream(clients).toList().toString());

        if (!gameTypesStore.all().containsKey(gameType)) return;

        IO.println("------------------------------------------");

        try {
            ServerPlayer[] players = new ServerPlayer[clients.length];
            var game = new OnlineTurnBasedGame(gameTypesStore.create(gameType), clients);

            for (int i = 0; i < clients.length; i++) {
                players[i] = new ServerPlayer(clients[i]);
                clients[i].addGame(new ImmutablePair<>(game, players[i]));
            }
            System.out.println("Starting OnlineTurnBasedGame");

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
        } catch (Exception e) {
            IO.println("ERROR: Failed to start OnlineTurnBasedGame");
            e.printStackTrace();
        }
    }

    @Override
    public List<NettyClient> onlineUsers() {
        return clientStore.all().stream().toList();
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
        gameChallenges.clear();
    }

    private void serverTask() {
        checkChallenges();
        checkSubscriptions();
        scheduler.schedule(this::serverTask, 500, TimeUnit.MILLISECONDS);
    }

    private void checkChallenges() {
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

    private void checkSubscriptions() {
        if (subscriptions.isEmpty()) return;

        List<String> keys = List.copyOf(subscriptions.keySet());
        Random ran = new Random();

        for (String key : keys) {
            List<String> userNames = subscriptions.get(key);
            if (userNames.size() < 2) continue;

            while (userNames.size() > 1) {
                int left = ran.nextInt(userNames.size());
                int right;
                do {
                    right = ran.nextInt(userNames.size());
                } while (left == right);

                String userLeft = userNames.get(left);
                String userRight = userNames.get(right);

                int first = Math.max(left, right);
                int second = Math.min(left, right);
                userNames.remove(first);
                userNames.remove(second);

                startGame(key, getUser(userLeft), getUser(userRight));
            }
        }
    }

    private NettyClient getUser(String username) {
        return clientStore.all().stream().filter(e -> e.name().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    private NettyClient getUser(long id) {
        return clientStore.get(id);
    }

    private void warnUserExpiredChallenge(NettyClient client, long challengeId) {
        client.send("SVR GAME CHALLENGE CANCELLED {CHALLENGENUMBER: \"" + challengeId + "\"}" + "\n");
    }

    private boolean isValidChallenge(GameChallenge gameChallenge) { // TODO move to challenge class
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
}
