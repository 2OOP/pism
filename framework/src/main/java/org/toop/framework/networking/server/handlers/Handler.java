package org.toop.framework.networking.server.handlers;

public interface Handler<T> {
    void handle(T message);
}
