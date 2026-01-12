package org.toop.framework.networking.server;

import org.toop.framework.networking.server.client.NettyClient;

import java.time.Duration;

@FunctionalInterface
public interface MatchExecutor {
    GameResultFuture submit(String gameType, Duration turnTime, NettyClient... clients);
}
