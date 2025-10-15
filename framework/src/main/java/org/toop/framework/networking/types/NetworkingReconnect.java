package org.toop.framework.networking.types;

import java.util.concurrent.TimeUnit;

public record NetworkingReconnect(int reconnectAttempts, long timeout, TimeUnit timeUnit) {}