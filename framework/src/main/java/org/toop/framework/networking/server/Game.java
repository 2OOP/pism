package org.toop.framework.networking.server;

import org.toop.framework.game.gameThreads.LocalThreadBehaviour;
import org.toop.framework.game.gameThreads.OnlineThreadBehaviour;
import org.toop.framework.game.gameThreads.ServerThreadBehaviour;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public class Game implements OnlineGame<TurnBasedGame> {

    private long id;
    private User[] users;
    private TurnBasedGame game;
    private ServerThreadBehaviour gameThread;

    public Game(TurnBasedGame game, User... users) {
        this.game = game;
        this.gameThread = new ServerThreadBehaviour(game, null);
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

    @Override
    public void start(){
        this.gameThread.start();
    }
}
