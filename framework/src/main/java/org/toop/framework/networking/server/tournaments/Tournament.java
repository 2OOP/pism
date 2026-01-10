package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.client.NettyClient;

import java.util.HashMap;

public interface Tournament {
//    void init(TournamentBuilder builder);
    boolean run(String gameType);
//    HashMap<NettyClient, Integer> end();
}
