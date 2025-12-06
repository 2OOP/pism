package org.toop.framework.eventbus;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.*;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.events.EventType;

/**
 * GlobalEventBus backed by the LMAX Disruptor for ultra-low latency, high-throughput event
 * publishing.
 */
public final class OldGlobalEventBus {
    private static final Logger logger = LogManager.getLogger(OldGlobalEventBus.class);

    /** Disruptor ring buffer size (must be power of two). */
    private static final int RING_BUFFER_SIZE = 1024 * 64;

    /** Disruptor instance. */
    private static final Disruptor<EventHolder> DISRUPTOR;

    /** Ring buffer used for publishing events. */
    private static final RingBuffer<EventHolder> RING_BUFFER;

    static {
        ThreadFactory threadFactory =
                r -> {
                    Thread t = new Thread(r, "EventBus-Disruptor");
                    t.setDaemon(true);
                    return t;
                };

        DISRUPTOR =
                new Disruptor<>(
                        EventHolder::new,
                        RING_BUFFER_SIZE,
                        threadFactory,
                        ProducerType.MULTI,
                        new BusySpinWaitStrategy());

        DISRUPTOR.handleEventsWith(
                (holder, seq, endOfBatch) -> {
                    if (holder.event != null) {
                        dispatchEvent(holder.event);
                        holder.event = null;
                    }
                });

        DISRUPTOR.start();
        RING_BUFFER = DISRUPTOR.getRingBuffer();
    }

    /** Prevent instantiation. */
    private OldGlobalEventBus() {}

    /** Wrapper used inside the ring buffer. */
    private static class EventHolder {
        EventType event;
    }

    /** Map of event class to type-specific listeners. */
    private static EventsHolder eventsHolder;

    public static void setEventsHolder(EventsHolder eventsHolder) {
        if (OldGlobalEventBus.eventsHolder != null) return;

        OldGlobalEventBus.eventsHolder = eventsHolder;
    }

    // ------------------------------------------------------------------------
    // Subscription
    // ------------------------------------------------------------------------
    public static <T extends EventType> void subscribe(ListenerHandler<T> listener) {
        logger.debug("Subscribing to {}: {}", listener.getListenerClass().getSimpleName(), listener.getListener().getClass().getSimpleName());
        eventsHolder.add(listener);
    }

    public static void unsubscribe(ListenerHandler<?> listener) {
        logger.debug("Unsubscribing from {}: {}", listener.getListenerClass().getSimpleName(), listener.getListener().getClass().getSimpleName());
        eventsHolder.remove(listener);
    }

    // ------------------------------------------------------------------------
    // Posting
    // ------------------------------------------------------------------------
    public static <T extends EventType> void post(T event) {
        dispatchEvent(event); // synchronous
    }

    public static <T extends EventType> void postAsync(T event) {
        long seq = RING_BUFFER.next();
        try {
            EventHolder holder = RING_BUFFER.get(seq);
            holder.event = event;
        } finally {
            RING_BUFFER.publish(seq);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends EventType> void callListener(ListenerHandler<?> raw, EventType event) {
        ListenerHandler<T> handler = (ListenerHandler<T>) raw;
        Consumer<T> listener = handler.getListener();

        T casted = (T) event;

        listener.accept(casted);
    }

    @SuppressWarnings("unchecked")
    private static void dispatchEvent(EventType event) {
        Class<?> clazz = event.getClass();

        logger.debug("Triggered event: {}", event.getClass().getSimpleName());

        CopyOnWriteArrayList<ListenerHandler<?>> classListeners = (CopyOnWriteArrayList<ListenerHandler<?>>) eventsHolder.get(clazz);
        if (classListeners != null) {
            for (ListenerHandler<?> listener : classListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
                    logger.warn("Exception while handling event: {}", event, e);
                }
            }
        }

        CopyOnWriteArrayList<ListenerHandler<?>> genericListeners = (CopyOnWriteArrayList<ListenerHandler<?>>) eventsHolder.get(Object.class);
        if (genericListeners != null) {
            for (ListenerHandler<?> listener : genericListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
                    logger.warn("Exception while handling event: {}", event, e);
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    public static void shutdown() {
        DISRUPTOR.shutdown();
        eventsHolder.reset();
    }

    public static void reset() {
        eventsHolder.reset();
    }

//    public static Map<Class<?>, CopyOnWriteArrayList<ListenerHandler<?>>> getAllListeners() {
////        return LISTENERS;
//    }
}
