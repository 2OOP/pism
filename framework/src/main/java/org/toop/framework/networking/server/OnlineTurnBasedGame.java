package org.toop.framework.networking.server;

import org.toop.framework.game.gameThreads.ServerThreadBehaviour;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.client.NettyClient;

public class OnlineTurnBasedGame implements OnlineGame<TurnBasedGame> {

    private long id;
    private NettyClient[] clients;
    private TurnBasedGame game;
    private ServerThreadBehaviour gameThread;

    public OnlineTurnBasedGame(TurnBasedGame game, NettyClient... clients) {
        this.game = game;
        this.gameThread = new ServerThreadBehaviour(
                game,
                (pair) -> notifyMoveMade(pair.getLeft(), pair.getRight()),
                (pair) -> notifyGameEnd(pair.getLeft(), pair.getRight())
        );
        this.clients = clients;
    }

    private void notifyMoveMade(String speler, int move){
        for (NettyClient client : clients) {
            client.send(String.format("SVR GAME MOVE {PLAYER: \"%s\", MOVE: \"%s\", DETAILS: \"<reactie spel op zet>\"}\n", speler, move));
        }
    }

    private void notifyGameEnd(GameState state, int winner){
        if (state == GameState.DRAW){
            for (NettyClient client : clients) {
                client.send(String.format("SVR GAME DRAW {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"NettyClient disconnected\"}\n"));
            }
        }
        else{
            clients[winner].send(String.format("SVR GAME WIN {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"NettyClient disconnected\"}\n"));
            clients[(winner + 1)%2].send(String.format("SVR GAME LOSS {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"NettyClient disconnected\"}\n"));
        }

    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public org.toop.framework.gameFramework.model.game.TurnBasedGame game() {
        return game;
    }

    @Override
    public NettyClient[] users() {
        return clients;
    }

    @Override
    public void start(){
        this.gameThread.start();
    }
}
