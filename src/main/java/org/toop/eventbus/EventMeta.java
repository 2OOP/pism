package org.toop.eventbus;

/**
 * Wraps an event with its type and a ready flag.
 */
public class EventMeta<T> {
    private final Class<T> type;
    private final Object event;
    private boolean ready;

    public EventMeta(Class<T> type, Object event) {
        this.type = type;
        this.event = event;
        this.ready = false; // default not ready
    }

    public Class<T> getType() {
        return type;
    }

    public Object getEvent() {
        return event;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return "ReadyEvent{" +
                "type=" + type.getSimpleName() +
                ", event=" + event +
                ", ready=" + ready +
                '}';
    }
}