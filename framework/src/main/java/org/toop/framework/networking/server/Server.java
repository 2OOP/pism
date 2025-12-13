package org.toop.framework.networking.server;

import org.toop.framework.game.gameThreads.OnlineThreadBehaviour;
import org.toop.framework.game.players.LocalPlayer;
import org.toop.framework.game.players.OnlinePlayer;
import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.time.Duration;

public class Server implements GameServer {

    final private Map<String, Class<? extends TurnBasedGame>> gameTypes;
    final private Map<Long, User> users = new ConcurrentHashMap<>();
    final private List<GameChallenge> gameChallenges = new CopyOnWriteArrayList<>();
    final private List<OnlineGame<TurnBasedGame>> games = new CopyOnWriteArrayList<>();

    final private Duration challengeDuration;
    final private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Server(Map<String, Class<? extends TurnBasedGame>> gameTypes, Duration challengeDuration) {
        this.gameTypes = gameTypes;
        this.challengeDuration = challengeDuration;

        scheduler.schedule(this::serverTask, 0, TimeUnit.MILLISECONDS);
    }

    private void serverTask() {
        checkChallenges();
        scheduler.schedule(this::serverTask, 500, TimeUnit.MILLISECONDS);
    }

    public void addUser(User user) {
        users.putIfAbsent(user.id(), user);
    }

    public void removeUser(User user) {
        users.remove(user.id());
    }

    public List<String> gameTypes() {
        return gameTypes.keySet().stream().toList();
    }

    public List<OnlineGame<TurnBasedGame>> ongoingGames() {
        return games;
    }

    public User getUser(String username) {
        return users.values().stream().filter(e -> e.name().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    public User getUser(long id) {
        return users.get(id);
    }

    public void challengeUser(String fromUser, String toUser, String gameType) {

        User from = getUser(fromUser);
        if (from == null) {
            return;
        }

        if (!gameTypes.containsKey(gameType)) {
            from.sendMessage("ERR gametype not found \n");
            return;
        }

        User to = getUser(toUser);
        if (to == null) {
            from.sendMessage("ERR user not found \n");
            return;
        }

        var ch = new GameChallenge(from, to, gameType, new GameChallengeTimer(challengeDuration));

        to.sendMessage(
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

    private void warnUserExpiredChallenge(User user, long challengeId) {
        user.sendMessage("SVR GAME CHALLENGE CANCELLED {CHALLENGENUMBER: \"" + challengeId + "\"}" + "\n");
    }

    private boolean isValidChallenge(GameChallenge gameChallenge) {
        for (var user : gameChallenge.getUsers()) {
            if (users.get(user.id()) == null) {
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

    public void startGame(String gameType, User... users) {
        if (!gameTypes.containsKey(gameType)) return;

        try {
            Player[] players = new Player[users.length];
            for (int i = 0; i < users.length; i++) {
                players[i] = new ServerPlayer(users[i]);
            }
            System.out.println("Starting Game");
            var game = new Game(gameTypes.get(gameType).getDeclaredConstructor().newInstance(), users);
            game.game().init(players);
            games.addLast(game);
            users[0].sendMessage(String.format("SVR GAME MATCH {PLAYERTOMOVE: \"%s\", GAMETYPE: \"%s\", OPPONENT: \"%s\"}\n",
                    users[0].name(),
                    gameType,
                    users[1].name()));
            users[1].sendMessage(String.format("SVR GAME MATCH {PLAYERTOMOVE: \"%s\", GAMETYPE: \"%s\", OPPONENT: \"%s\"}\n",
                    users[0].name(),
                    gameType,
                    users[0].name()));
            game.start();
        } catch (Exception ignored) {}
    }

//    public void checkGames() {
//        for (int i = games.size() - 1; i >= 0; i--) {
//            var game = games.get(i);
//            if (game.game().getWinner() >= 0) games.remove(i);
//        }
//    }

    public List<User> onlineUsers() {
        return users.values().stream().toList();
    }

    public void closeServer() {
        scheduler.shutdown();
        gameChallenges.clear();
        games.clear();
        users.clear();
        gameTypes.clear();
    }
}
