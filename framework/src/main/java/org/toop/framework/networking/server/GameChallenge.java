package org.toop.framework.networking.server;

import org.toop.framework.SnowflakeGenerator;

public class GameChallenge {
    private final long id = SnowflakeGenerator.nextId(); // I don't need this, but the tournament server uses it...

    private final ServerUser from;
    private final ServerUser to;
    private final String gameType;
    private final SimpleTimer timer;

    private boolean isChallengeAccepted = false;

    public GameChallenge(ServerUser from, ServerUser to, String gameType, SimpleTimer timer) {
        this.from = from;
        this.to = to;
        this.gameType = gameType;
        this.timer = timer;
    }

    public long id() {
        return id;
    }

    public ServerUser[] getUsers() {
        return new  ServerUser[]{from, to};
    }

    public void forceExpire() {
        timer.forceExpire();
    }

    public String acceptChallenge() {
        isChallengeAccepted = true;
        timer.forceExpire();
        return gameType;
    }

    public boolean isExpired() {
        return timer.isExpired();
    }
}