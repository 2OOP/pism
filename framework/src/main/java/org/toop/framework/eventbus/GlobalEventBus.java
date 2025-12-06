package org.toop.framework.eventbus;

import org.toop.framework.eventbus.events.EventType;

public class GlobalEventBus {
    private static final EventBus<EventType> INSTANCE = new DisruptorEventBus<>(new DisruptorEventsHolder());

    private GlobalEventBus() {}

    public static EventBus<EventType> get() {
        return INSTANCE;
    }
}
