package org.toop.framework.networking.server.challenges.gamechallenge;

import org.toop.framework.utils.SimpleTimer;

import java.time.Instant;
import java.time.Duration;

public class GameChallengeTimer implements SimpleTimer {

    private final Instant createdAt;
    private final Duration timeout;

    private boolean isExpired = false;

    public GameChallengeTimer(Duration duration) {
        this.createdAt = Instant.now();
        this.timeout = duration;
    }

    @Override
    public void forceExpire() {
        this.isExpired = true;
    }

    @Override
    public boolean isExpired() {
        if (this.isExpired) return true;
        return Instant.now().isAfter(createdAt.plus(timeout));
    }

    @Override
    public long secondsRemaining() {
        return Duration.between(Instant.now(), createdAt.plus(timeout)).toSeconds();
    }
}
