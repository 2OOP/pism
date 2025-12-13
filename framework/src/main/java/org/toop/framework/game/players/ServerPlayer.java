package org.toop.framework.game.players;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.gameFramework.model.player.AbstractPlayer;
import org.toop.framework.gameFramework.model.player.Player;
import org.toop.framework.networking.server.client.NettyClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServerPlayer extends AbstractPlayer {
    private NettyClient client;
    private CompletableFuture<Long> lastMove;

    public ServerPlayer(NettyClient client) {
        super(client.name());
        this.client = client;
    }

    public void setMove(long move) {
        lastMove.complete(move);
    }

    @Override
    public Player deepCopy() {
        return null;
    }

    @Override
    public long getMove(TurnBasedGame game) {
        lastMove = new CompletableFuture<>();
        System.out.println("Sending yourturn");
        client.send("SVR GAME YOURTURN {TURNMESSAGE: \"<bericht voor deze beurt>\"}\n");
        try {
            return lastMove.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
