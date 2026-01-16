package org.toop.framework.networking.server;

import org.toop.framework.game.gameThreads.ServerThreadBehaviour;
import org.toop.framework.gameFramework.GameState;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.client.NettyClient;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class OnlineTurnBasedGame implements OnlineGame<TurnBasedGame> {

    private long id;
    private NettyClient[] clients;
    private NettyClient[] admins;
    private TurnBasedGame game;
    private ServerThreadBehaviour gameThread;

    private final CompletableFuture<Integer> resultFuture;

    public OnlineTurnBasedGame(NettyClient[] admins, TurnBasedGame game, CompletableFuture<Integer> resultFuture, Duration timeOut, NettyClient... clients) {
        this.game = game;
        this.gameThread = new ServerThreadBehaviour(
                game,
                (pair) -> notifyMoveMade(pair.getLeft(), pair.getRight()),
                (pair) -> notifyGameEnd(pair.getLeft(), pair.getRight()),
                timeOut
        );
        this.resultFuture = resultFuture;
        this.clients = clients;
        this.admins = admins;
    }

    private void notifyMoveMade(String speler, int move){
        for (NettyClient admin : admins) {
            admin.send(String.format("SVR GAME MOVE {PLAYER: \"%s\", MOVE: \"%s\", DETAILS: \"<reactie spel op zet>\"}", speler, move));
        }
        for (NettyClient client : clients) {
            client.send(String.format("SVR GAME MOVE {PLAYER: \"%s\", MOVE: \"%s\", DETAILS: \"<reactie spel op zet>\"}", speler, move));
        }
    }

    private void notifyGameEnd(GameState state, int winner) {
        if (state == GameState.DRAW) {
            Arrays.stream(admins).forEach(a -> a.send("SVR GAME END"));

            for (NettyClient client : clients) {
                client.send("SVR GAME DRAW {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"<comment>\"}");
            }
        } else {
            Arrays.stream(admins).forEach(a -> a.send("SVR GAME END"));
            clients[winner].send("SVR GAME WIN {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"<comment>\"}");
            clients[(winner+1)%2].send("SVR GAME LOSS {PLAYERONESCORE: \"<score speler1>\", PLAYERTWOSCORE: \"<score speler2>\", COMMENT: \"<comment>\"}");
        }

        // Remove game from clients
        for (NettyClient client : clients) {
            admins = null;
            client.clearGame();
        }

        if (resultFuture != null) {
            if (state.equals(GameState.DRAW)) resultFuture.complete(-1); // Return -1 if draw
            else resultFuture.complete(winner); // Return number for winner's index
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
    public void start() {
        this.gameThread.start();
    }
}
