package org.toop.framework.eventbus;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.events.UniqueEvent;

/**
 * GlobalEventBus backed by the LMAX Disruptor for ultra-low latency, high-throughput event
 * publishing.
 */
public final class GlobalEventBus {
    private static final Logger logger = LogManager.getLogger(GlobalEventBus.class);

    /** Map of event class to type-specific listeners. */
    private static final Map<Class<?>, CopyOnWriteArrayList<ListenerHandler<?>>>
            LISTENERS = new ConcurrentHashMap<>();

    /** Map of event class to Snowflake-ID-specific listeners. */
    private static final Map<
                    Class<?>, ConcurrentHashMap<Long, Consumer<? extends UniqueEvent>>>
            UUID_LISTENERS = new ConcurrentHashMap<>();

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
    private GlobalEventBus() {}

    /** Wrapper used inside the ring buffer. */
    private static class EventHolder {
        EventType event;
    }

    // ------------------------------------------------------------------------
    // Subscription
    // ------------------------------------------------------------------------
    public static <T extends EventType> void subscribe(ListenerHandler<T> listener) {
        logger.debug("Subscribing to {}: {}", listener.getListenerClass().getSimpleName(), listener.getListener().getClass().getSimpleName());
        LISTENERS.computeIfAbsent(listener.getListenerClass(), _ -> new CopyOnWriteArrayList<>()).add(listener);
    }

    // TODO
    public static <T extends UniqueEvent> void subscribeById(
            Class<T> eventClass, long eventId, Consumer<T> listener) {
        UUID_LISTENERS
                .computeIfAbsent(eventClass, _ -> new ConcurrentHashMap<>())
                .put(eventId, listener);
    }

    public static void unsubscribe(ListenerHandler<?> listener) {
        logger.debug("Unsubscribing from {}: {}", listener.getListenerClass().getSimpleName(), listener.getListener().getClass().getSimpleName());
        LISTENERS.getOrDefault(listener.getListenerClass(), new CopyOnWriteArrayList<>())
                .remove(listener);
        LISTENERS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    // TODO
    public static <T extends UniqueEvent> void unsubscribeById(
            Class<T> eventClass, long eventId) {
        Map<Long, Consumer<? extends UniqueEvent>> map = UUID_LISTENERS.get(eventClass);
        if (map != null) map.remove(eventId);
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

        CopyOnWriteArrayList<ListenerHandler<?>> classListeners = LISTENERS.get(clazz);
        if (classListeners != null) {
            for (ListenerHandler<?> listener : classListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
//                    e.printStackTrace();
                }
            }
        }

        CopyOnWriteArrayList<ListenerHandler<?>> genericListeners = LISTENERS.get(Object.class);
        if (genericListeners != null) {
            for (ListenerHandler<?> listener : genericListeners) {
                try {
                    callListener(listener, event);
                } catch (Throwable e) {
                    // e.printStackTrace();
                }
            }
        }

        if (event instanceof UniqueEvent snowflakeEvent) {
            Map<Long, Consumer<? extends UniqueEvent>> map = UUID_LISTENERS.get(clazz);
            if (map != null) {
                Consumer<UniqueEvent> listener =
                        (Consumer<UniqueEvent>) map.remove(snowflakeEvent.getIdentifier());
                if (listener != null) {
                    try {
                        listener.accept(snowflakeEvent);
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    public static void shutdown() {
        DISRUPTOR.shutdown();
        LISTENERS.clear();
        UUID_LISTENERS.clear();
    }

    public static void reset() {
        LISTENERS.clear();
        UUID_LISTENERS.clear();
    }

    public static Map<Class<?>, CopyOnWriteArrayList<ListenerHandler<?>>> getAllListeners() {
        return LISTENERS;
    }
}
