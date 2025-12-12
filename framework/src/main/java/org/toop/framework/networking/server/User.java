package org.toop.framework.networking.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class User implements ServerUser {
    final private long id;
    private String name;
    private final List<Game> games = new ArrayList<>();
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
        games.add(game);
    }

    @Override
    public void removeGame(Game game) {
        games.remove(game);
    }

    @Override
    public Game[] games() {
        return games.toArray(new Game[0]);
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
