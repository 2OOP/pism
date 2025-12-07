package org.toop.framework.eventbus.bus;

import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.store.SubscriberStore;
import org.toop.framework.eventbus.subscriber.Subscriber;

import java.util.function.Consumer;

public class DefaultEventBus implements EventBus {
    private final Logger logger;
    private final SubscriberStore eventsHolder;

    public DefaultEventBus(Logger logger, SubscriberStore eventsHolder) {
        this.logger = logger;
        this.eventsHolder = eventsHolder;
    }

    @Override
    public void subscribe(Subscriber<?, ?> subscriber) {
        eventsHolder.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber<?, ?> subscriber) {
        eventsHolder.remove(subscriber);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void post(T event) {
        Class<T> eventType = (Class<T>) event.getClass();
        var subs = eventsHolder.get(eventType);
        if (subs != null) {
            for (Subscriber<?, ?> subscriber : subs) {
                Class<T> eventClass = (Class<T>) subscriber.event();
                Consumer<EventType> action = (Consumer<EventType>) subscriber.handler();

                action.accept((EventType) eventClass.cast(event));
            }
        }
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
