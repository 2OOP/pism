package org.toop.framework.networking.server;

import org.toop.framework.game.gameThreads.ServerThreadBehaviour;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;

public class Game implements OnlineGame<TurnBasedGame> {

    private long id;
    private User[] users;
    private TurnBasedGame game;
    private ServerThreadBehaviour gameThread;

    public Game(TurnBasedGame game, User... users) {
        this.game = game;
        this.gameThread = new ServerThreadBehaviour(
                game,
                (pair) -> notifyMoveMade(pair.getLeft(), pair.getRight()),
                (pair) -> notifyGameEnd(pair.getLeft(), pair.getRight())
        );
        this.users = users;
    }

    private void notifyMoveMade(String speler, int move){
        for (User user : users) {
            user.sendMessage(String.format("SVR GAME MOVE {PLAYER: \"%s\", MOVE: \"%s\", DETAILS: \"<reactie spel op zet>\"}\n", speler, move));
        }
    }

    private void notifyGameEnd(GameState state, int winner){
        if (state == GameState.DRAW){
            for (User user : users) {
                user.sendMessage(String.format("SVR GAME DRAW {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"Client disconnected\"}\n"));
            }
        }
        else{
            users[winner].sendMessage(String.format("SVR GAME WIN {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"Client disconnected\"}\n"));
            users[(winner + 1)%2].sendMessage(String.format("SVR GAME LOSS {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"Client disconnected\"}\n"));
        }

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
