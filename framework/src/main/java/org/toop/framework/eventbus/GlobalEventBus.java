package org.toop.framework.eventbus;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.events.UniqueEvent;

/**
 * GlobalEventBus backed by the LMAX Disruptor for ultra-low latency, high-throughput event
 * publishing.
 */
public final class GlobalEventBus {

    /** Map of event class to type-specific listeners. */
    private static final Map<Class<?>, CopyOnWriteArrayList<Consumer<? super EventType>>>
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

        // Single consumer that dispatches to subscribers
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
    public static <T extends EventType> Consumer<? super EventType> subscribe(
            Class<T> eventClass, Consumer<T> listener) {

        CopyOnWriteArrayList<Consumer<? super EventType>> list =
                LISTENERS.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>());

        Consumer<? super EventType> wrapper = event -> listener.accept(eventClass.cast(event));
        list.add(wrapper);
        return wrapper;
    }

    public static Consumer<? super EventType> subscribe(Consumer<Object> listener) {
        Consumer<? super EventType> wrapper = event -> listener.accept(event);
        LISTENERS.computeIfAbsent(Object.class, _ -> new CopyOnWriteArrayList<>()).add(wrapper);
        return wrapper;
    }

    public static <T extends UniqueEvent> void subscribeById(
            Class<T> eventClass, long eventId, Consumer<T> listener) {
        UUID_LISTENERS
                .computeIfAbsent(eventClass, _ -> new ConcurrentHashMap<>())
                .put(eventId, listener);
    }

    public static void unsubscribe(Object listener) {
        LISTENERS.values().forEach(list -> list.remove(listener));
    }

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
    private static void dispatchEvent(EventType event) {
        Class<?> clazz = event.getClass();

        // class-specific listeners
        CopyOnWriteArrayList<Consumer<? super EventType>> classListeners = LISTENERS.get(clazz);
        if (classListeners != null) {
            for (Consumer<? super EventType> listener : classListeners) {
                try {
                    listener.accept(event);
                } catch (Throwable ignored) {
                }
            }
        }

        // generic listeners
        CopyOnWriteArrayList<Consumer<? super EventType>> genericListeners =
                LISTENERS.get(Object.class);
        if (genericListeners != null) {
            for (Consumer<? super EventType> listener : genericListeners) {
                try {
                    listener.accept(event);
                } catch (Throwable ignored) {
                }
            }
        }

        // snowflake listeners
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
}
