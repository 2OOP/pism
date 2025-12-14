package org.toop.framework.networking.server.client;

import org.toop.framework.networking.server.OnlineTurnBasedGame;
import org.toop.framework.utils.Pair;

public interface Client<G, P> {
    long id();

    String name();
    void setName(String name);

    OnlineTurnBasedGame game();
    P player();

    void setGame(Pair<G, P> gamePair);
    void clearGame();

    void send(String message);
}
