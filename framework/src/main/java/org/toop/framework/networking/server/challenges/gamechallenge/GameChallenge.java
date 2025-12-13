package org.toop.framework.networking.server.challenges.gamechallenge;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.networking.server.client.NettyClient;
import org.toop.framework.utils.SimpleTimer;

public class GameChallenge {
    private final long id = SnowflakeGenerator.nextId(); // I don't need this, but the tournament server uses it...

    private final NettyClient from;
    private final NettyClient to;
    private final String gameType;
    private final SimpleTimer timer;

    private boolean isChallengeAccepted = false;

    public GameChallenge(NettyClient from, NettyClient to, String gameType, SimpleTimer timer) {
        this.from = from;
        this.to = to;
        this.gameType = gameType;
        this.timer = timer;
    }

    public long id() {
        return id;
    }

    public NettyClient[] getUsers() {
        return new NettyClient[]{from, to};
    }

    public void forceExpire() {
        timer.forceExpire();
    }

    public String acceptChallenge() {
        isChallengeAccepted = true;
        timer.forceExpire();
        return gameType;
    }

    public boolean isChallengeAccepted() {
        return isChallengeAccepted;
    }

    public boolean isExpired() {
        return timer.isExpired();
    }
}