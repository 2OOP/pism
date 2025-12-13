package org.toop.framework.networking.server.handlers;

import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.networking.server.OnlineTurnBasedGame;
import org.toop.framework.networking.server.Server;
import org.toop.framework.networking.server.client.Client;
import org.toop.framework.networking.server.parsing.ParsedMessage;
import org.toop.framework.utils.Utils;

public class MessageHandler implements Handler<ParsedMessage> {

    private final Server server;
    private final Client<OnlineTurnBasedGame, ServerPlayer> client;

    public MessageHandler(Server server, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void handle(ParsedMessage message) {
        switch (message.command()) {
            case "ping" -> client.send("PONG");
            case "login" -> handleLogin(message, client);
            case "get" -> handleGet(message, client);
            case "subscribe" -> handleSubscribe(message, client);
            case "move" -> handleMove(message, client);
            case "challenge" -> handleChallenge(message, client);
            case "message" -> handleMessage(message, client);
            case "help" -> handleHelp(message, client);
            default -> client.send("ERROR Unknown command");
        }
    }

    // DO NOT INVERT
    private boolean hasArgs(String... args) {
        return (args.length >= 1);
    }

    private void handleLogin(ParsedMessage p, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        if (!hasArgs(p.args())) return;

        client.setName(p.args()[0]);
    }

    private void handleSubscribe(ParsedMessage p, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        if (!hasArgs(p.args())) return;

        server.subscribeClient(p.args()[0], client.name());
    }

    private void handleHelp(ParsedMessage p, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        // TODO
    }

    private void handleMessage(ParsedMessage p, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        // TODO
    }

    private void handleGet(ParsedMessage p, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        if (!hasArgs(p.args())) return;

        switch (p.args()[0]) {
            case "playerlist" -> {
                var names = server.onlineUsers().stream().map(Client::name).iterator();
                client.send("SVR PLAYERLIST " + Utils.returnQuotedString(names));
            }
            case "gamelist" -> {
                var names = server.gameTypes().stream().iterator();
                client.send("SVR GAMELIST " + Utils.returnQuotedString(names));
            }
        }
    }

    private void handleChallenge(ParsedMessage p, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        if (!hasArgs(p.args())) return;
        if (p.args().length < 2) return;

        if (p.args()[0].equalsIgnoreCase("accept")) {
            try {
                long id = Long.parseLong(p.args()[1]);

                if (id <= 0) {
                    client.send("ERR id must be a positive number");
                    return;
                }

                server.acceptChallenge(id);

            } catch (NumberFormatException e) {
                client.send("ERR id is not a valid number or too big");
                return;
            }
            return;
        }

        server.challengeClient(client.name(), p.args()[0], p.args()[1]);
    }

    private void handleMove(ParsedMessage p, Client<OnlineTurnBasedGame, ServerPlayer> client) {
        if(!hasArgs(p.args())) return;

        // TODO check if not number
        client.player().setMove(1L << Integer.parseInt(p.args()[0]));
    }
}
