package org.toop.framework.utils;

public interface SimpleTimer {
    void forceExpire();
    boolean isExpired();
    long secondsRemaining();
}
