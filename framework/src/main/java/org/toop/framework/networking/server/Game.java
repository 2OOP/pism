package org.toop.framework.networking.server;

import org.toop.framework.game.BitboardGame;

public class Game implements OnlineGame {

    private long id;
    private User[] users;
    private GameDefinition<BitboardGame<?>> game;

    public Game(GameDefinition game, User... users) {
        this.game = game;
        this.users = users;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public GameDefinition game() {
        return game;
    }

    @Override
    public User[] users() {
        return users;
    }
}
