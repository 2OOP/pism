package org.toop.framework.networking.server;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GameServer<GAMETYPE, CLIENT, CHALLENGEIDTYPE> {
    GameResultFuture startGame(String gameType, Duration turnTime, CLIENT... clients);

    void addClient(CLIENT client);
    void removeClient(CLIENT client);

    void challengeClient(String fromClientName, String toClientName, String gameTypeKey);
    void acceptChallenge(CHALLENGEIDTYPE challengeId);

    void subscribeClient(String clientName, String gameTypeKey);
    void unsubscribeClient(String clientName);

    List<String> gameTypes();
    List<OnlineGame<GAMETYPE>> ongoingGames();

    List<CLIENT> onlineUsers();
    void shutdown();
}
