package org.toop.framework.eventbus;

import org.apache.logging.log4j.LogManager;
import org.toop.framework.eventbus.bus.DisruptorEventBus;
import org.toop.framework.eventbus.bus.EventBus;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.store.DefaultSubscriberStore;
import org.toop.framework.eventbus.subscriber.Subscriber;

public class GlobalEventBus implements EventBus {
    private static final EventBus INSTANCE = new DisruptorEventBus(
            LogManager.getLogger(DisruptorEventBus.class),
            new DefaultSubscriberStore()
    );

    private GlobalEventBus() {}

    public static EventBus get() {
        return INSTANCE;
    }

    @Override
    public void subscribe(Subscriber<? extends EventType> listener) {
        INSTANCE.subscribe(listener);
    }

    @Override
    public void unsubscribe(Subscriber<? extends EventType> listener) {
        INSTANCE.unsubscribe(listener);
    }

    @Override
    public <T extends EventType> void post(T event) {
        INSTANCE.post(event);
    }

    @Override
    public void shutdown() {
        INSTANCE.shutdown();
    }

    @Override
    public void reset() {
        INSTANCE.reset();
    }
}
