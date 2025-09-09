package org.toop;

import com.google.common.eventbus.EventBus;

public enum GlobalEventBus {
    INSTANCE;

    private final EventBus eventBus = new EventBus("global-bus");

    public EventBus get() {
        return eventBus;
    }
}