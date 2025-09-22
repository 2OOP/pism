package org.toop.eventbus;

import org.toop.eventbus.events.EventWithUuid;
import org.toop.eventbus.events.IEvent;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * GlobalEventBus is a high-throughput, thread-safe event bus for publishing and subscribing
 * to events within the application.
 *
 * <p>It supports:</p>
 * <ul>
 *     <li>Type-specific subscriptions via {@link #subscribe(Class, Consumer)}</li>
 *     <li>UUID-specific subscriptions via {@link #subscribeById(Class, String, Consumer)}</li>
 *     <li>Asynchronous posting of events with automatic queueing and fallback</li>
 * </ul>
 *
 * <p><b>Performance note:</b> Directly using {@link GlobalEventBus} is possible,
 * but for safer type handling, automatic UUID management, and easier unsubscription,
 * it is recommended to use {@link EventPublisher} whenever possible.</p>
 *
 * <p>The bus maintains a fixed pool of worker threads that continuously process queued events.</p>
 */
public final class GlobalEventBus {

    /** Number of worker threads, set to the number of available CPU cores. */
    private static final int WORKERS = Runtime.getRuntime().availableProcessors();

    /** Queue for asynchronous event processing. */
    private static final BlockingQueue<IEvent> EVENT_QUEUE = new LinkedBlockingQueue<>(WORKERS * 1024);

    /** Map of event class to type-specific listeners. */
    private static final Map<Class<?>, CopyOnWriteArrayList<Consumer<? super IEvent>>> LISTENERS = new ConcurrentHashMap<>();

    /** Map of event class to UUID-specific listeners. */
    private static final Map<Class<?>, ConcurrentHashMap<String, Consumer<? extends EventWithUuid>>> UUID_LISTENERS = new ConcurrentHashMap<>();

    /** Thread pool for worker threads processing queued events. */
    private static final ExecutorService WORKER_POOL = Executors.newFixedThreadPool(WORKERS, r -> {
        Thread t = new Thread(r, "EventBus-Worker-" + r.hashCode());
        t.setDaemon(true);
        return t;
    });

    // Initialize worker threads
    static {
        for (int i = 0; i < WORKERS; i++) {
            WORKER_POOL.submit(GlobalEventBus::workerLoop);
        }
    }

    /** Private constructor to prevent instantiation. */
    private GlobalEventBus() {}

    /** Continuously processes events from the queue and dispatches them to listeners. */
    private static void workerLoop() {
        try {
            while (true) {
                IEvent event = EVENT_QUEUE.take();
                dispatchEvent(event);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Subscribes a type-specific listener for all events of a given class.
     *
     * @param eventClass the class of events to subscribe to
     * @param listener   the action to execute when the event is posted
     * @param <T>        the event type
     * @return the provided listener for possible unsubscription
     */
    public static <T extends IEvent> Consumer<T> subscribe(Class<T> eventClass, Consumer<T> listener) {
        CopyOnWriteArrayList<Consumer<? super IEvent>> list =
                LISTENERS.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>());
        list.add(event -> listener.accept(eventClass.cast(event)));
        return listener;
    }

    /**
     * Subscribes a generic listener for all events (no type filtering).
     *
     * @param listener the action to execute on any event
     * @return the provided listener for possible unsubscription
     */
    public static Consumer<Object> subscribe(Consumer<Object> listener) {
        LISTENERS.computeIfAbsent(Object.class, _ -> new CopyOnWriteArrayList<>())
                .add(listener);
        return listener;
    }

    /**
     * Subscribes a listener for a specific {@link EventWithUuid} identified by its UUID.
     *
     * @param eventClass the class of the UUID event
     * @param eventId    the UUID of the event to listen for
     * @param listener   the action to execute when the event with the matching UUID is posted
     * @param <T>        the event type extending EventWithUuid
     */
    public static <T extends EventWithUuid> void subscribeById(Class<T> eventClass, String eventId, Consumer<T> listener) {
        UUID_LISTENERS
                .computeIfAbsent(eventClass, _ -> new ConcurrentHashMap<>())
                .put(eventId, listener);
    }

    /**
     * Unsubscribes a previously registered listener.
     *
     * @param listener the listener to remove
     */
    public static void unsubscribe(Object listener) {
        LISTENERS.values().forEach(list -> list.remove(listener));
    }

    /**
     * Unsubscribes a UUID-specific listener.
     *
     * @param eventClass the class of the UUID event
     * @param eventId    the UUID of the listener to remove
     * @param <T>        the event type extending EventWithUuid
     */
    public static <T extends EventWithUuid> void unsubscribeById(Class<T> eventClass, String eventId) {
        Map<String, Consumer<? extends EventWithUuid>> map = UUID_LISTENERS.get(eventClass);
        if (map != null) map.remove(eventId);
    }

    /**
     * Posts an event synchronously to all subscribed listeners.
     *
     * @param event the event instance to post
     * @param <T>   the event type
     */
    public static <T extends IEvent> void post(T event) {
        dispatchEvent(event);
    }

    /**
     * Posts an event asynchronously by adding it to the internal queue.
     * If the queue is full, the event is dispatched synchronously.
     *
     * @param event the event instance to post
     * @param <T>   the event type
     */
    public static <T extends IEvent> void postAsync(T event) {
        if (!EVENT_QUEUE.offer(event)) {
            dispatchEvent(event);
        }
    }

    /** Dispatches an event to all type-specific, generic, and UUID-specific listeners. */
    @SuppressWarnings("unchecked")
    private static void dispatchEvent(IEvent event) {
        Class<?> clazz = event.getClass();

        CopyOnWriteArrayList<Consumer<? super IEvent>> classListeners = LISTENERS.get(clazz);
        if (classListeners != null) {
            for (Consumer<? super IEvent> listener : classListeners) {
                try { listener.accept(event); } catch (Throwable ignored) {}
            }
        }

        CopyOnWriteArrayList<Consumer<? super IEvent>> genericListeners = LISTENERS.get(Object.class);
        if (genericListeners != null) {
            for (Consumer<? super IEvent> listener : genericListeners) {
                try { listener.accept(event); } catch (Throwable ignored) {}
            }
        }

        if (event instanceof EventWithUuid uuidEvent) {
            Map<String, Consumer<? extends EventWithUuid>> map = UUID_LISTENERS.get(clazz);
            if (map != null) {
                Consumer<EventWithUuid> listener = (Consumer<EventWithUuid>) map.remove(uuidEvent.eventId());
                if (listener != null) {
                    try { listener.accept(uuidEvent); } catch (Throwable ignored) {}
                }
            }
        }
    }

    /**
     * Shuts down the bus immediately, clearing all listeners and queued events.
     * Worker threads are stopped.
     */
    public static void shutdown() {
        WORKER_POOL.shutdownNow();
        LISTENERS.clear();
        UUID_LISTENERS.clear();
        EVENT_QUEUE.clear();
    }

    /**
     * Clears all listeners and UUID-specific subscriptions without stopping worker threads.
     */
    public static void reset() {
        LISTENERS.clear();
        UUID_LISTENERS.clear();
    }
}
