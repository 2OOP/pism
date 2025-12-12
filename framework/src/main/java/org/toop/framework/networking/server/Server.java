package org.toop.framework.networking.server;

import org.toop.framework.game.BitboardGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server implements GameServer {

    final private Map<String, GameDefinition<BitboardGame<?>>> gameTypes;
    public List<OnlineGame> games = new ArrayList<>();
    final private Map<Long, ServerUser> users = new ConcurrentHashMap<>();

    public Server(Map<String, GameDefinition<BitboardGame<?>>> gameTypes) {
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

//    public List<OnlineGame<BitboardGame<?>>> ongoingGames() {
//        return List.of();
//    }

    public void startGame(String gameType, User... users) {
        if (!gameTypes.containsKey(gameType)) return;

        try {
            var game = new Game(gameTypes.get(gameType).create(), users);
            games.addLast(new Game(game, users));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String[] onlineUsers() {
        return users.values().stream().map(ServerUser::name).toArray(String[]::new);
    }
}
