package org.toop.framework.networking.server;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

public class User implements ServerUser {
    final private long id;
    private String name;
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
    public void setName(String name) {
        this.name = name;
    }

    public ChannelHandlerContext ctx() {
        return connectionContext;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.connectionContext = ctx;
    }

}
