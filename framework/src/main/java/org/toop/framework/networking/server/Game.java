package org.toop.framework.networking.server;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public class Game implements OnlineGame<TurnBasedGame> {

    private long id;
    private User[] users;
    private TurnBasedGame game;

    public Game(TurnBasedGame game, User... users) {
        this.game = game;
        this.users = users;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public TurnBasedGame game() {
        return game;
    }

    @Override
    public User[] users() {
        return users;
    }
}
