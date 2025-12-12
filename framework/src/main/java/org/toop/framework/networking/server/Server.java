package org.toop.framework.networking.server;

import org.toop.framework.game.players.LocalPlayer;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.time.Duration;

public class Server implements GameServer {

    final private Map<String, Class<? extends TurnBasedGame>> gameTypes;
    final private Map<Long, ServerUser> users = new ConcurrentHashMap<>();
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

    public void addUser(ServerUser user) {
        users.putIfAbsent(user.id(), user);
    }

    public void removeUser(ServerUser user) {
        users.remove(user.id());
    }

    public String[] gameTypes() {
        return gameTypes.keySet().toArray(new String[0]);
    }

    public List<OnlineGame<TurnBasedGame>> ongoingGames() {
        return games;
    }

    public ServerUser getUser(String username) {
        return users.values().stream().filter(e -> e.name().equalsIgnoreCase(username)).findFirst().get();
    }

    public ServerUser getUser(long id) {
        return users.get(id);
    }

    public void challengeUser(String fromUser, String toUser) {
        ServerUser from = getUser(fromUser);
        if (from == null) {
            return;
        }
        ServerUser to = getUser(toUser);
        if (to == null) {
            return;
        }

        gameChallenges.addLast(new GameChallenge(from, to, new GameChallengeTimer(challengeDuration)));
    }

    public void checkChallenges() {
        for (int i = gameChallenges.size() - 1; i >= 0; i--) {
            if (gameChallenges.get(i).isExpired()) gameChallenges.remove(i);
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
                players[i] = new LocalPlayer(users[i].name());
            }

            var game = new Game(gameTypes.get(gameType).getDeclaredConstructor().newInstance(), users);
            game.game().init(players);
            games.addLast(game);

        } catch (Exception ignored) {}
    }

//    public void checkGames() {
//        for (int i = games.size() - 1; i >= 0; i--) {
//            var game = games.get(i);
//            if (game.game().getWinner() >= 0) games.remove(i);
//        }
//    }

    public String[] onlineUsers() {
        return users.values().stream().map(ServerUser::name).toArray(String[]::new);
    }
}
