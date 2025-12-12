package org.toop.framework.networking.server;

public class GameChallenge {
    private final ServerUser from;
    private final ServerUser to;
    private final SimpleTimer timer;

    private boolean isChallengeAccepted = false;

    public GameChallenge(ServerUser from, ServerUser to, SimpleTimer timer) {
        this.from = from;
        this.to = to;
        this.timer = timer;
    }

    public void acceptChallenge() {
        isChallengeAccepted = true;
        timer.forceExpire();
    }

    public boolean isExpired() {
        return timer.isExpired();
    }
}