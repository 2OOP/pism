package org.toop.framework.networking.server.client;

import io.netty.channel.ChannelHandlerContext;
import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.networking.server.OnlineTurnBasedGame;
import org.toop.framework.utils.Pair;

public class NettyClient implements Client<OnlineTurnBasedGame, ServerPlayer> {
    final private long id;
    private ChannelHandlerContext ctx;
    private String name;
    private Pair<OnlineTurnBasedGame, ServerPlayer> gamePair;

    public NettyClient(long userId, String name) {
        this.id = userId;
        this.name = name;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void setGame(Pair<OnlineTurnBasedGame, ServerPlayer> gamePair) {
        this.gamePair = gamePair;
    }

    @Override
    public void clearGame() {
        this.gamePair = null;
    }

    @Override
    public OnlineTurnBasedGame game() {
        if (this.gamePair == null) {
            return null;
        }
        return this.gamePair.getLeft();
    }

    @Override
    public ServerPlayer player() {
        return this.gamePair.getRight();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void send(String message) {
        IO.println(message);
        ctx.channel().writeAndFlush(message + "\r\n");
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
