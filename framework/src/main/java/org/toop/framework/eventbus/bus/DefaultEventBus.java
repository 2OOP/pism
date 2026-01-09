package org.toop.framework.eventbus.bus;

import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.store.SubscriberStore;
import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class DefaultEventBus implements AsyncEventBus {
    private final Logger logger;
    private final SubscriberStore eventsHolder;

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public DefaultEventBus(Logger logger, SubscriberStore eventsHolder) {
        this.logger = logger;
        this.eventsHolder = eventsHolder;
    }

    @Override
    public void subscribe(Subscriber<? extends EventType> subscriber) {
        eventsHolder.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber<? extends EventType> subscriber) {
        eventsHolder.remove(subscriber);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EventType> void post(T event) {
        Class<T> eventType = (Class<T>) event.getClass();
        var subs = eventsHolder.get(eventType);
        if (subs != null) {
            for (Subscriber<?> subscriber : subs) {
                Class<T> eventClass = (Class<T>) subscriber.event();
                Consumer<EventType> action = (Consumer<EventType>) subscriber.handler();

                action.accept(eventClass.cast(event));
            }
        }
    }

    @Override
    public <T extends EventType> void asyncPost(T event) {
        asyncExecutor.submit(() -> post(event));
    }

    @Override
    public void shutdown() {
        eventsHolder.reset();
    }

    @Override
    public void reset() {
        eventsHolder.reset();
    }

}
