package org.toop.eventbus;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.toop.eventbus.events.EventWithUuid;

import java.math.BigInteger;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventPublisherStressTest {

    public record HeavyEvent(String payload, String eventId) implements EventWithUuid {
        @Override
        public java.util.Map<String, Object> result() {
            return java.util.Map.of("payload", payload, "eventId", eventId);
        }
    }

    private static final int THREADS = 1;
    private static final long EVENTS_PER_THREAD = 2_000_000_000;

    @Tag("stress")
    @Test
    void extremeConcurrencyTest_progressWithMemory() throws InterruptedException {
        AtomicLong counter = new AtomicLong(0); // Big numbers safety
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        GlobalEventBus.subscribeAndRegister(HeavyEvent.class, _ -> counter.incrementAndGet());

        BigInteger totalEvents = BigInteger.valueOf(THREADS)
                .multiply(BigInteger.valueOf(EVENTS_PER_THREAD));

        long startTime = System.currentTimeMillis();

        // Monitor thread for EPS and memory
        Thread monitor = new Thread(() -> {
            long lastCount = 0;
            long lastTime = startTime;

            Runtime runtime = Runtime.getRuntime();

            while (counter.get() < totalEvents.longValue()) {
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

                long now = System.currentTimeMillis();
                long completed = counter.get();
                long eventsThisSecond = completed - lastCount;
                double eps = eventsThisSecond / ((now - lastTime) / 1000.0);

                // Memory usage
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                long maxMemory = runtime.maxMemory();
                double usedPercent = usedMemory * 100.0 / maxMemory;

                System.out.printf(
                        "Progress: %d/%d (%.2f%%), EPS: %.0f, Memory Used: %.2f MB (%.2f%%)\n",
                        completed,
                        totalEvents.longValue(),
                        completed * 100.0 / totalEvents.doubleValue(),
                        eps,
                        usedMemory / 1024.0 / 1024.0,
                        usedPercent
                );

                lastCount = completed;
                lastTime = now;
            }
        });
        monitor.setDaemon(true);
        monitor.start();

        // Submit events
        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                for (int i = 0; i < EVENTS_PER_THREAD; i++) {
                    new EventPublisher<>(HeavyEvent.class, "payload-" + i).postEvent();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(20, TimeUnit.MINUTES); // allow extra time for huge tests

        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;

        System.out.println("Posted " + totalEvents + " events in " + durationSeconds + " seconds");
        double averageEps = totalEvents.doubleValue() / durationSeconds;
        System.out.printf("Average EPS: %.0f%n", averageEps);

        assertEquals(totalEvents.longValue(), counter.get());
    }

    @Tag("stress")
    @Test
    void efficientExtremeConcurrencyTest() throws InterruptedException {
        final int THREADS = Runtime.getRuntime().availableProcessors(); // threads ≈ CPU cores
        final int EVENTS_PER_THREAD = 5000;

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        ConcurrentLinkedQueue<HeavyEvent> processedEvents = new ConcurrentLinkedQueue<>();

        long start = System.nanoTime();

        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                for (int i = 0; i < EVENTS_PER_THREAD; i++) {
                    new EventPublisher<>(HeavyEvent.class, "payload-" + i)
                            .onEventById(HeavyEvent.class, processedEvents::add)
                            .postEvent();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long end = System.nanoTime();
        double durationSeconds = (end - start) / 1_000_000_000.0;

        BigInteger totalEvents = BigInteger.valueOf((long) THREADS).multiply(BigInteger.valueOf(EVENTS_PER_THREAD));
        double eps = totalEvents.doubleValue() / durationSeconds;

        System.out.printf("Posted %s events in %.3f seconds%n", totalEvents, durationSeconds);
        System.out.printf("Throughput: %.0f events/sec%n", eps);

        // Memory snapshot
        Runtime rt = Runtime.getRuntime();
        System.out.printf("Used memory: %.2f MB%n", (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0);

        // Ensure all events were processed
        assertEquals(totalEvents.intValue(), processedEvents.size());
    }

    @Tag("stress")
    @Test
    void constructorCacheVsReflection() throws Throwable {
        int iterations = 1_000_000;
        long startReflect = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            // Reflection every time
            HeavyEvent.class.getDeclaredConstructors()[0].newInstance("payload", "uuid-" + i);
        }
        long endReflect = System.nanoTime();

        long startHandle = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            // Using cached MethodHandle
            EventPublisher<HeavyEvent> ep = new EventPublisher<>(HeavyEvent.class, "payload-" + i);
        }
        long endHandle = System.nanoTime();

        System.out.println("Reflection: " + (endReflect - startReflect) / 1_000_000 + " ms");
        System.out.println("MethodHandle Cache: " + (endHandle - startHandle) / 1_000_000 + " ms");
    }
}
