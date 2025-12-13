package org.toop.framework.networking.server.client;

import org.toop.framework.networking.server.Game;
import org.toop.framework.utils.Pair;

public interface Client<G, P> {
    long id();

    String name();
    void setName(String name);

    Game game();
    P player();

    void addGame(Pair<G, P> gamePair);
    void clearGame();

    void send(String message);
}
