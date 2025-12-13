package org.toop.framework.networking.server.connectionHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.networking.server.OnlineTurnBasedGame;
import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.networking.server.handlers.Handler;
import org.toop.framework.networking.server.parsing.ParsedMessage;
import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.client.Client;
import org.toop.framework.networking.server.parsing.Parser;

import java.util.Arrays;

public class NettyClientSession extends SimpleChannelInboundHandler<String> implements ClientSession<OnlineTurnBasedGame, ServerPlayer> {

    private final NettyClient client;
    private final Server server;
    private final Handler<ParsedMessage> handler;

    public NettyClientSession(NettyClient client, Server server, Handler<ParsedMessage> handler) {
        this.client = client;
        this.server = server;
        this.handler = handler;
    }

    @Override
    public Client<OnlineTurnBasedGame, ServerPlayer> client() {
        return client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("Welcome " + client.id() + " please login" + "\n");

        client.setCtx(ctx);
        server.addClient(client); // TODO set correct name on login
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {

        IO.println(msg);

        ParsedMessage p = Parser.parse(msg);
        if (p == null) return;

        IO.println(p.command() + " " + Arrays.toString(p.args()));

        handler.handle(p);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        server.removeClient(client);
    }
}
