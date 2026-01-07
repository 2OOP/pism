package org.toop.framework.networking.connection.types;

public record ServerCommand(long clientId, String command) {}
