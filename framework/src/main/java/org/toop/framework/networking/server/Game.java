package org.toop.framework.networking.server;

import org.toop.framework.game.gameThreads.ServerThreadBehaviour;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public class Game implements OnlineGame<TurnBasedGame> {

    private long id;
    private User[] users;
    private TurnBasedGame game;
    private ServerThreadBehaviour gameThread;

    public Game(TurnBasedGame game, User... users) {
        this.game = game;
        this.gameThread = new ServerThreadBehaviour(game, (pair) -> notifyMoveMade(pair.getLeft(), pair.getRight()));
        this.users = users;
    }

    private void notifyMoveMade(String speler, int move){
        users[0].sendMessage(String.format("SVR GAME MOVE {PLAYER: \"%s\", DETAILS: \"<reactie spel op zet>\", MOVE: \"%s\"}", speler, move));
        users[1].sendMessage(String.format("SVR GAME MOVE {PLAYER: \"%s\", DETAILS: \"<reactie spel op zet>\", MOVE: \"%s\"}", speler, move));
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
