package org.toop.framework.networking.server.stores;

import org.toop.framework.game.players.ServerPlayer;
import org.toop.framework.networking.server.Game;
import org.toop.framework.networking.server.client.Client;

public interface ClientStore<ID, T extends Client<Game, ServerPlayer>> extends Store<ID, T> {}
