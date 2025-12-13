package org.toop.framework.networking.server;

import io.netty.channel.ChannelHandlerContext;
import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.utils.Pair;

public class User implements ServerUser {
    final private long id;
    private String name;
    private Pair<Game, ServerPlayer> gamePair;
    private ChannelHandlerContext connectionContext;

    public User(long userId, String name) {
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
    public void addGame(Pair<Game, ServerPlayer> gamePair) {
        if (this.gamePair == null) {
            this.gamePair = gamePair;
        }
    }

    @Override
    public void removeGame() {
        this.gamePair = null;
    }

    @Override
    public Game game() {
        if (this.gamePair == null) {
            return null;
        }
        return this.gamePair.getLeft();
    }

    public ServerPlayer serverPlayer() {
        return this.gamePair.getRight();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void sendMessage(String message) {
        IO.println(message);
        ctx().channel().writeAndFlush(message);
    }

    public ChannelHandlerContext ctx() {
        return connectionContext;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.connectionContext = ctx;
    }


}
