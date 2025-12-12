package org.toop.framework.networking.server;

public interface MessageStore {
    void add(String message);
    String get();
    void reset();
}
