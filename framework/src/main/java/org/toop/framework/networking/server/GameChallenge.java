package org.toop.framework.networking.server;

import org.toop.framework.SnowflakeGenerator;

public class GameChallenge {
    private final long id = SnowflakeGenerator.nextId(); // I don't need this, but the tournament server uses it...

    private final User from;
    private final User to;
    private final String gameType;
    private final SimpleTimer timer;

    private boolean isChallengeAccepted = false;

    public GameChallenge(User from, User to, String gameType, SimpleTimer timer) {
        this.from = from;
        this.to = to;
        this.gameType = gameType;
        this.timer = timer;
    }

    public long id() {
        return id;
    }

    public User[] getUsers() {
        return new User[]{from, to};
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