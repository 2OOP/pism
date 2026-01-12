package org.toop.framework.networking.server;

import com.google.gson.Gson;
import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.challenges.gamechallenge.GameChallenge;
import org.toop.framework.networking.server.challenges.gamechallenge.GameChallengeTimer;
import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.stores.ClientStore;
import org.toop.framework.networking.server.stores.SubscriptionStore;
import org.toop.framework.networking.server.stores.TurnBasedGameStore;
import org.toop.framework.networking.server.stores.TurnBasedGameTypeStore;
import org.toop.framework.networking.server.tournaments.*;
import org.toop.framework.networking.server.tournaments.matchmakers.DoubleRoundRobinMatchMaker;
import org.toop.framework.networking.server.tournaments.scoresystems.*;
import org.toop.framework.utils.ImmutablePair;

import java.util.*;
import java.util.concurrent.*;
import java.time.Duration;

public class Server implements GameServer<TurnBasedGame, NettyClient, Long> {

    final private TurnBasedGameTypeStore gameTypesStore;
    final private ClientStore<Long, NettyClient> clientStore;
    final private List<GameChallenge> gameChallenges = new CopyOnWriteArrayList<>();
    final private TurnBasedGameStore gameStore;

    final private SubscriptionStore subscriptionStore;

    final private Duration challengeDuration;
    final private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final List<NettyClient> admins = new ArrayList<>();

    public Server(
            Duration challengeDuration,
            TurnBasedGameTypeStore turnBasedGameTypeStore,
            ClientStore<Long, NettyClient> clientStore,
            TurnBasedGameStore gameStore,
            SubscriptionStore subStore
    ) {
        this.gameTypesStore = turnBasedGameTypeStore;
        this.challengeDuration = challengeDuration;
        this.clientStore = clientStore;
        this.gameStore = gameStore;
        this.subscriptionStore = subStore;

        scheduler.schedule(this::serverTask, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void addClient(NettyClient client) {
        if (admins.isEmpty()) admins.addLast(client);
        clientStore.add(client);
    }

    @Override
    public void removeClient(NettyClient client) {
        admins.remove(client);
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
                startGame(challenge.acceptChallenge(), Duration.ofSeconds(10), challenge.getUsers());
                break;
            }
        }
    }

    @Override
    public void subscribeClient(String clientName, String gameTypeKey) {

        if (!gameTypesStore.all().containsKey(gameTypeKey)) {
            return;
        }

        subscriptionStore.add(new ImmutablePair<>(gameTypeKey, clientName));
    }

    @Override
    public void unsubscribeClient(String clientName) {
        subscriptionStore.remove(clientName);
    }

    @Override
    public GameResultFuture startGame(String gameType, Duration turnTime, NettyClient... clients) {
        if (!gameTypesStore.all().containsKey(gameType)) return null;

        try {

            ServerPlayer[] players = new ServerPlayer[clients.length];

            var gameResult = new CompletableFuture<Integer>();

            var game = new OnlineTurnBasedGame(
                    getAdmins().toArray(NettyClient[]::new),
                    gameTypesStore.create(gameType),
                    gameResult,
                    clients
            );

            var grfReturn = new GameResultFuture(game, gameResult);

            for (int i = 0; i < clients.length; i++) {
                players[i] = new ServerPlayer(clients[i]);
                clients[i].setGame(new ImmutablePair<>(game, players[i]));
            }

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
            return grfReturn;
        } catch (Exception e) {
            IO.println("ERROR: Failed to start OnlineTurnBasedGame");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<NettyClient> onlineUsers() {
        return clientStore.all().stream().toList();
    }

    public List<NettyClient> getAdmins() {
        return new ArrayList<>(admins); // Clone so the list can't be edited.
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
        if (subscriptionStore.allKeys().isEmpty()) return;

        Random ran = new Random();

        for (String key : subscriptionStore.allKeys()) {
            List<String> userNames = (List<String>) subscriptionStore.allValues(key);
            if (userNames.size() < 2) continue;

            while (userNames.size() > 1) {
                int left = ran.nextInt(userNames.size());
                int right;
                do {
                    right = ran.nextInt(userNames.size());
                } while (left == right);

                String userLeft = userNames.get(left);
                String userRight = userNames.get(right);

                // Remove user if he is in a game
                boolean userInGame = false;

                if (getUser(userLeft).game() != null) {
                    userNames.remove(userLeft);
                    userInGame = true;
                } else if (getUser(userRight).game() != null) {
                    userNames.remove(userRight);
                    userInGame = true;
                }

                if  (userInGame) { continue; }

                int first = Math.max(left, right);
                int second = Math.min(left, right);

                userNames.remove(first);
                userNames.remove(second);

                startGame(key, Duration.ofSeconds(10), getUser(userLeft), getUser(userRight));
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

    public void startTournament(String gameType, NettyClient requestor, boolean shuffle) {
        if (!admins.contains(requestor)) {
            requestor.send("ERR you do not have the privileges to start a tournament");
            return;
        }

        var tournamentUsers = new ArrayList<>(onlineUsers());
        tournamentUsers.removeIf(admins::contains);

        Tournament tournament = new Tournament.Builder()
                .matchExecutor(this::startGame)
                .tournamentRunner(new AsyncTournamentRunner())
                .matchMaker(new DoubleRoundRobinMatchMaker())
                .addScoreSystem(new MatchCountScoreSystem())
                .addScoreSystem(new WinCountScoreSystem())
                .addScoreSystem(new DrawCountScoreSystem())
                .addScoreSystem(new LoseCountScoreSystem())
                .resultBroadcaster(this::endTournament)
                .turnTimeout(Duration.ofSeconds(10))
                .addPlayers(tournamentUsers.toArray(NettyClient[]::new))
                .addAdmins(admins.toArray(NettyClient[]::new))
                .build();

        new Thread(() -> tournament.run(gameType)).start();
    }

    public void endTournament(List<IntegerScoreSystem> systems) {
        if (systems.isEmpty()) return;

        Map<String, List<ImmutablePair<String, Integer>>> combined = new HashMap<>();

        for (var system : systems) {
            for (var player : system.getScore().keySet()) {
                combined.putIfAbsent(player.name(), new ArrayList<>());
                combined.get(player.name()).addLast(new ImmutablePair<>(system.scoreName(), system.getScore().get(player)));
            }
        }

        List<String> names = new ArrayList<>();
        List<String> systemNames = new ArrayList<>();
        List<List<Integer>> scores = new ArrayList<>();

        for (var player : combined.entrySet()) {
            names.addLast(player.getKey());
            scores.addLast(new ArrayList<>());
            for (var system : player.getValue()) {
                if (!systemNames.contains(system.getLeft())) systemNames.addLast(system.getLeft());
                scores.getLast().addLast(system.getRight());
            }
        }

        Gson gson = new Gson();

        String namesJson = gson.toJson(names);
        String systemNamesJson = gson.toJson(systemNames);
        String scoresJson = gson.toJson(scores);

        String msg = String.format(
                "SVR RESULTS {GAMETYPE: \"%s\", USERS: %s, SCORETYPES: %s, SCORES: %s, TOURNAMENT: 1}",
                "none", // TODO gametype
                namesJson,
                systemNamesJson,
                scoresJson
        );

        for (var user : onlineUsers()) {
            user.send(msg);
        }
    }
}
