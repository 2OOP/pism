package org.toop.server;

public record CommandEvent(Server.Command command, String[] args, Server.Message result) {}
