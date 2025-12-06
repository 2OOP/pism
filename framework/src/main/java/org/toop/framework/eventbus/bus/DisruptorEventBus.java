package org.toop.framework.eventbus.bus;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.subscriber.Subscriber;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.holder.EventsHolder;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class DisruptorEventBus implements EventBus {
    /** Wrapper used inside the ring buffer. */
    private static class EventHolder<T> {
        T event;
    }

    private final Logger logger;
    private final EventsHolder eventsHolder;

    private final Disruptor<EventHolder<?>> disruptor;
    private final RingBuffer<EventHolder<?>> ringBuffer;

    public DisruptorEventBus(Logger logger, EventsHolder eventsHolder) {
        this.logger = logger;
        this.eventsHolder = eventsHolder;

        ThreadFactory threadFactory =
                r -> {
                    Thread t = new Thread(r, "EventBus-Disruptor");
                    t.setDaemon(true);
                    return t;
                };

        disruptor = getEventHolderDisruptor(threadFactory);

        disruptor.start();
        this.ringBuffer = disruptor.getRingBuffer();
    }

    private Disruptor<EventHolder<?>> getEventHolderDisruptor(ThreadFactory threadFactory) {
        int RING_BUFFER_SIZE = 1024 * 64;
        Disruptor<EventHolder<?>> disruptor = new Disruptor<>(
                EventHolder::new,
                RING_BUFFER_SIZE,
                threadFactory,
                ProducerType.MULTI,
                new BusySpinWaitStrategy());

        disruptor.handleEventsWith(
                (holder, _, _) -> {
                    if (holder.event != null) {
                        dispatchEvent(holder.event);
                        holder.event = null;
                    }
                });
        return disruptor;
    }

    @Override
    public void subscribe(Subscriber<?, ?> listener) {
        eventsHolder.add(listener);
    }

    @Override
    public void unsubscribe(Subscriber<?, ?> listener) {
        eventsHolder.remove(listener);
    }

    @Override
    public <T> void post(T event) {
        long seq = ringBuffer.next();
        try {
            EventHolder<T> holder = (EventHolder<T>) ringBuffer.get(seq);
            holder.event = event;
        } finally {
            ringBuffer.publish(seq);
        }
    }

    @Override
    public void shutdown() {
        disruptor.shutdown();
        eventsHolder.reset();
    }

    @Override
    public void reset() {
        eventsHolder.reset();
    }

    private <T> void dispatchEvent(T event) {
        CopyOnWriteArrayList<Subscriber<?, ?>> classListeners = (CopyOnWriteArrayList<Subscriber<?, ?>>) eventsHolder.get(event.getClass());
        if (classListeners != null) {
            for (Subscriber<?, ?> listener : classListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
                    logger.warn("Exception while handling event: {}", event, e);
                }
            }
        }

        CopyOnWriteArrayList<Subscriber<?, ?>> genericListeners = (CopyOnWriteArrayList<Subscriber<?, ?>>) eventsHolder.get(Object.class);
        if (genericListeners != null) {
            for (Subscriber<?, ?> listener : genericListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
                    logger.warn("Exception while handling event: {}", event, e);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    private <T> void callListener(Subscriber<?, ?> subscriber, T event) {
        Class<T> eventClass = (Class<T>) subscriber.getEvent();
        Consumer<EventType> action = (Consumer<EventType>) subscriber.getAction();

        action.accept((EventType) eventClass.cast(event));
    }
}
