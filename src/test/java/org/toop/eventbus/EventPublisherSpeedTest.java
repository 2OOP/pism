package org.toop.eventbus;

import org.junit.jupiter.api.Test;
import org.toop.eventbus.events.EventWithUuid;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EventPublisherPerformanceTest {

    public record PerfEvent(String name, String eventId) implements EventWithUuid {
        @Override
        public java.util.Map<String, Object> result() {
            return java.util.Map.of("name", name, "eventId", eventId);
        }
    }

    @Test
    void testEventCreationSpeed() {
        int iterations = 10_000;
        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            new EventPublisher<>(PerfEvent.class, "event-" + i);
        }

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        System.out.println("Created " + iterations + " events in " + durationMs + " ms");
        assertTrue(durationMs < 500, "Event creation too slow");
    }

    @Test
    void testEventPostSpeed() {
        int iterations = 100_000;
        AtomicInteger counter = new AtomicInteger(0);

        GlobalEventBus.subscribe(PerfEvent.class, e -> counter.incrementAndGet());

        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            new EventPublisher<>(PerfEvent.class, "event-" + i).postEvent();
        }

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        System.out.println("Posted " + iterations + " events in " + durationMs + " ms");
        assertTrue(counter.get() == iterations, "Not all events were received");
        assertTrue(durationMs < 1000, "Posting events too slow");
    }

    @Test
    void testConcurrentEventPostSpeed() throws InterruptedException {
        int threads = 20;
        int eventsPerThread = 5_000;
        AtomicInteger counter = new AtomicInteger(0);

        GlobalEventBus.subscribe(PerfEvent.class, e -> counter.incrementAndGet());

        Thread[] workers = new Thread[threads];

        long start = System.nanoTime();

        for (int t = 0; t < threads; t++) {
            workers[t] = new Thread(() -> {
                for (int i = 0; i < eventsPerThread; i++) {
                    new EventPublisher<>(PerfEvent.class, "event-" + i).postEvent();
                }
            });
            workers[t].start();
        }

        for (Thread worker : workers) {
            worker.join();
        }

        long end = System.nanoTime();
        long durationMs = (end - start) / 1_000_000;

        System.out.println("Posted " + (threads * eventsPerThread) + " events concurrently in " + durationMs + " ms");
        assertTrue(counter.get() == threads * eventsPerThread, "Some events were lost");
        assertTrue(durationMs < 5000, "Concurrent posting too slow");
    }
}