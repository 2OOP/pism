package org.toop.framework.networking.server.tournaments;

import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.utils.ImmutablePair;

public class TournamentMatch extends ImmutablePair<NettyClient, NettyClient> {
    public TournamentMatch(NettyClient a, NettyClient b) {
        super(a, b);
    }

    public NettyClient getClient0() {
        return getLeft();
    }

    public NettyClient getClient1() {
        return getRight();
    }
}
