package org.toop.framework.networking.types;

import java.util.concurrent.TimeUnit;

public record NetworkingConnector(String host, int port, int reconnectAttempts, long timeout, TimeUnit timeUnit) {}