package org.toop.framework.eventbus;

import org.apache.logging.log4j.LogManager;
import org.toop.framework.eventbus.events.EventType;

public class GlobalEventBus {
    private static final EventBus<EventType> INSTANCE = new DisruptorEventBus<>(
            LogManager.getLogger(DisruptorEventBus.class),
            new DisruptorEventsHolder()
    );

    private GlobalEventBus() {}

    public static EventBus<EventType> get() {
        return INSTANCE;
    }
}
