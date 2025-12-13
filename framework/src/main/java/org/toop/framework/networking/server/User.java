package org.toop.framework.networking.server;

import io.netty.channel.ChannelHandlerContext;
import org.toop.framework.game.players.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class User implements ServerUser {
    final private long id;
    private String name;
    private final Map<Game, ServerPlayer> game = new HashMap<>();
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
    public void addGame(Game game, ServerPlayer player) {
        if (this.game.isEmpty()) {
            this.game.put(game, player);
        }
    }

    @Override
    public void removeGame() {
        this.game.clear();
    }

    @Override
    public Game game() {
        return this.game.keySet().iterator().next();
    }

    public ServerPlayer serverPlayer() {
        return this.game.values().iterator().next();
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
