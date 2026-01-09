package org.toop.framework.networking.server;

import java.util.List;

public interface GameServer<GAMETYPE, CLIENT, CHALLENGEIDTYPE> {
    void startGame(String gameType, CLIENT... clients);

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
