package org.toop.framework.networking.server;

import org.toop.framework.game.BitboardGame;
import org.toop.framework.game.players.LocalPlayer;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements GameServer {

    final private Map<String, Class<? extends TurnBasedGame>> gameTypes;
    final private List<OnlineGame<TurnBasedGame>> games = new ArrayList<>();
    final private Map<Long, ServerUser> users = new ConcurrentHashMap<>();

    public Server(Map<String, Class<? extends TurnBasedGame>> gameTypes) {
        this.gameTypes = gameTypes;
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

    public String[] onlineUsers() {
        return users.values().stream().map(ServerUser::name).toArray(String[]::new);
    }
}
