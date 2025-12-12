package org.toop.framework.networking.server;

public interface SimpleTimer {
    void forceExpire();
    boolean isExpired();
    long secondsRemaining();
}
