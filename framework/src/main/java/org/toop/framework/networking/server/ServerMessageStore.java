package org.toop.framework.networking.server;

import java.util.Queue;

public class ServerMessageStore implements MessageStore {

    Queue<String> messageQueue;

    public ServerMessageStore(Queue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void add(String message) {
        messageQueue.offer(message);
    }

    @Override
    public String get() {
        return messageQueue.poll();
    }

    @Override
    public void reset() {
        messageQueue.clear();
    }
}
