package org.toop.framework.networking.server;

import io.netty.channel.ChannelHandlerContext;

public class User implements ServerUser {
    final private long id;
    private String name;
    private Game game;
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
    public void addGame(Game game) {
        if (this.game == null) {
            this.game = game;
        }
    }

    @Override
    public void removeGame() {
        this.game = null;
    }

    @Override
    public Game game() {
        return this.game;
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
