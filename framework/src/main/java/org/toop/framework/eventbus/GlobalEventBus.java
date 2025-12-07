package org.toop.framework.eventbus;

import org.apache.logging.log4j.LogManager;
import org.toop.framework.eventbus.bus.DisruptorEventBus;
import org.toop.framework.eventbus.bus.EventBus;
import org.toop.framework.eventbus.holder.AsyncEventsHolder;
import org.toop.framework.eventbus.subscriber.Subscriber;

public class GlobalEventBus implements EventBus {
    private static final EventBus INSTANCE = new DisruptorEventBus(
            LogManager.getLogger(DisruptorEventBus.class),
            new AsyncEventsHolder()
    );

    private GlobalEventBus() {}

    public static EventBus get() {
        return INSTANCE;
    }

    @Override
    public void subscribe(Subscriber<?, ?> listener) {
        get().subscribe(listener);
    }

    @Override
    public void unsubscribe(Subscriber<?, ?> listener) {
        get().unsubscribe(listener);
    }

    @Override
    public <T> void post(T event) {
        get().post(event);
    }

    @Override
    public void shutdown() {
        get().shutdown();
    }

    @Override
    public void reset() {
        get().reset();
    }
}
