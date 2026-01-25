package org.toop.framework.eventbus;

import org.apache.logging.log4j.LogManager;
import org.toop.framework.eventbus.bus.AsyncEventBus;
import org.toop.framework.eventbus.bus.DefaultEventBus;
import org.toop.framework.eventbus.bus.DisruptorEventBus;
import org.toop.framework.eventbus.bus.EventBus;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.store.DefaultSubscriberStore;
import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalEventBus implements AsyncEventBus {
    private static final AsyncEventBus INSTANCE = new DefaultEventBus(
            LogManager.getLogger(DefaultEventBus.class),
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
    public <T extends EventType> void asyncPost(T event) {
        INSTANCE.asyncPost(event);
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
