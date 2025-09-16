package org.toop.eventbus;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe registry for storing events and tracking readiness of event types.
 */
public class EventRegistry {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private static final Map<Class<?>, CopyOnWriteArrayList<EventEntry<?>>> eventHistory =
            new ConcurrentHashMap<>();

    private static final Map<Class<?>, Boolean> readyStates = new ConcurrentHashMap<>();

    /**
     * Stores an event in the registry. Safe for concurrent use.
     */
    public static <T> void storeEvent(EventMeta<T> eventMeta) {
        logger.info("Storing event: {}", eventMeta.toString());
        eventHistory
                .computeIfAbsent(eventMeta.getType(), k -> new CopyOnWriteArrayList<>())
                .add(new EventEntry<>(eventMeta));
    }

    /**
     * Marks a specific event type as ready (safe to post).
     */
    public static <T> void markReady(Class<T> type) {
        logger.info("Marking event as ready: {}", type.toString());
        readyStates.put(type, true);
    }

    /**
     * Marks a specific event type as not ready (posting will fail).
     */
    public static <T> void markNotReady(Class<T> type) {
        logger.info("Marking event as not ready: {}", type.toString());
        readyStates.put(type, false);
    }

    /**
     * Returns true if this event type is marked ready.
     */
    public static <T> boolean isReady(Class<T> type) {
        return readyStates.getOrDefault(type, false);
    }

    /**
     * Gets all stored events of a given type.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<EventEntry<T>> getEvents(Class<T> type) {
        return (List<EventEntry<T>>) (List<?>) eventHistory
                .getOrDefault(type, new CopyOnWriteArrayList<>());
    }

    /**
     * Gets the most recent event of a given type, or null if none exist.
     */
    @SuppressWarnings("unchecked")
    public static <T> EventEntry<T> getLastEvent(Class<T> type) {
        List<EventEntry<?>> entries = eventHistory.get(type);
        if (entries == null || entries.isEmpty()) {
            return null;
        }
        return (EventEntry<T>) entries.getLast();
    }

    /**
     * Clears the stored events for a given type.
     */
    public static <T> void clearEvents(Class<T> type) {
        logger.info("Clearing events: {}", type.toString());
        eventHistory.remove(type);
    }

    /**
     * Clears all events and resets readiness.
     */
    public static void reset() {
        logger.info("Resetting event registry events");
        eventHistory.clear();
        readyStates.clear();
    }

    /**
     * Wrapper for stored events, with a ready flag for per-event state.
     */
    public static class EventEntry<T> {
        private final T event;
        private volatile boolean ready = false;

        public EventEntry(T event) {
            this.event = event;
        }

        public T getEvent() {
            return event;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }

        @Override
        public String toString() {
            return "EventEntry{" +
                    "event=" + event +
                    ", ready=" + ready +
                    '}';
        }
    }
}
