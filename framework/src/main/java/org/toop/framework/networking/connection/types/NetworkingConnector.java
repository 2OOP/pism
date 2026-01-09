package org.toop.framework.networking.connection.types;

import java.util.concurrent.TimeUnit;

public record NetworkingConnector(String host, int port, int reconnectAttempts, long timeout, TimeUnit timeUnit) {}