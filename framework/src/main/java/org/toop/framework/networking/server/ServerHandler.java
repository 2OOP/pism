package org.toop.framework.networking.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private final User user;
    private final Server server;

    public ServerHandler(User user, Server server) {
        this.user = user;
        this.server = server;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush("WELCOME " + user.id() + "\n");

        user.setCtx(ctx);
        server.addUser(user); // TODO set correct name on login
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        ParsedMessage p = parse(msg);
        if (p == null) return;

        IO.println(p.command() + " " + Arrays.toString(p.args()));

        switch (p.command()) {
            case "ping" -> ctx.writeAndFlush("PONG\n");
            case "login" -> handleLogin(p);
            case "get" -> handleGet(p);
            case "subscribe" -> handleSubscribe(p);
            case "move" -> handleMove(p);
            case "challenge" -> handleChallenge(p);
            case "message" -> handleMessage(p);
            case "help" -> handleHelp(p);
            default -> ctx.writeAndFlush("ERROR Unknown command\n");
        }
    }

    private boolean allowedArgs(String... args) {
        if (args.length < 1) return false;
        return true;
    }

    private void handleLogin(ParsedMessage p) {

        if (!allowedArgs(p.args())) return;

        user.setName(p.args()[0]);
    }

    private void handleGet(ParsedMessage p) {
        if (!allowedArgs(p.args())) return;

        switch (p.args()[0]) {
            case "playerlist" -> user.ctx().writeAndFlush(Arrays.toString(server.onlineUsers()));
            case "gamelist" -> user.ctx().writeAndFlush(Arrays.toString(server.gameTypes()));
        }
    }

    private void handleSubscribe(ParsedMessage p) {
        // TODO
    }

    private void handleHelp(ParsedMessage p) {
        // TODO
    }

    private void handleMessage(ParsedMessage p) {
        // TODO
    }

    private void handleChallenge(ParsedMessage p) {
        if(!allowedArgs(p.args())) return;
        if (p.args().length < 2) return;

        if (p.args()[0].equalsIgnoreCase("accept")) {
            try {
                long id = Long.parseLong(p.args()[1]);

                if (id <= 0) {
                    user.sendMessage("ERR id must be a positive number");
                    return;
                }

                server.acceptChallenge(id);

            } catch (NumberFormatException e) {
                user.sendMessage("ERR id is not a valid number or too big");
                return;
            }
            return;
        }

        server.challengeUser(user.name(), p.args()[0], p.args()[1]);
    }

    private void handleMove(ParsedMessage p) {
        // TODO
    }

    private ParsedMessage parse(String msg) {
        // TODO, what if empty string.

        if (msg.isEmpty()) return null;

        msg = msg.trim().toLowerCase();

        List<String> parts = new LinkedList<>(List.of(msg.split(" ")));

        if (parts.size() > 1) {
            String command = parts.removeFirst();
            return new ParsedMessage(command, parts.toArray(String[]::new));
        }
        else {
            return new ParsedMessage(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        server.removeUser(user);
    }
}
