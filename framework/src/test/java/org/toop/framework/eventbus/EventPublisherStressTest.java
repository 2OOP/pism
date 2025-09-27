package org.toop.framework.eventbus;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.toop.framework.eventbus.events.EventWithSnowflake;

import java.math.BigInteger;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventPublisherStressTest {

    /** Top-level record to ensure runtime type matches subscription */
    public record HeavyEvent(String payload, long eventSnowflake) implements EventWithSnowflake {
        @Override
        public java.util.Map<String, Object> result() {
            return java.util.Map.of("payload", payload, "eventId", eventSnowflake);
        }

        @Override
        public long eventSnowflake() {
            return this.eventSnowflake;
        }
    }

    public record HeavyEventSuccess(String payload, long eventSnowflake) implements EventWithSnowflake {
        @Override
        public java.util.Map<String, Object> result() {
            return java.util.Map.of("payload", payload, "eventId", eventSnowflake);
        }

        @Override
        public long eventSnowflake() {
            return eventSnowflake;
        }
    }

    private static final int THREADS = 32;
    private static final long EVENTS_PER_THREAD = 10_000_000;

    @Tag("stress")
    @Test
    void extremeConcurrencySendTest_progressWithMemory() throws InterruptedException {
        LongAdder counter = new LongAdder();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        BigInteger totalEvents = BigInteger.valueOf(THREADS)
                .multiply(BigInteger.valueOf(EVENTS_PER_THREAD));

        long startTime = System.currentTimeMillis();

        // Monitor thread for EPS and memory
        Thread monitor = new Thread(() -> {
            long lastCount = 0;
            long lastTime = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            while (counter.sum() < totalEvents.longValue()) {
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}

                long now = System.currentTimeMillis();
                long completed = counter.sum();
                long eventsThisPeriod = completed - lastCount;
                double eps = eventsThisPeriod / ((now - lastTime) / 1000.0);

                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                double usedPercent = usedMemory * 100.0 / runtime.maxMemory();

                System.out.printf(
                        "Progress: %d/%d (%.2f%%), EPS: %.0f, Memory Used: %.2f MB (%.2f%%)%n",
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

        var listener = new EventFlow().listen(HeavyEvent.class, _ -> counter.increment());

        // Submit events asynchronously
        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                for (int i = 0; i < EVENTS_PER_THREAD; i++) {
                    var _ = new EventFlow().addPostEvent(HeavyEvent.class, "payload-" + i)
                            .asyncPostEvent();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        listener.getResult();

        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;

        System.out.println("Posted " + totalEvents + " events in " + durationSeconds + " seconds");
        double averageEps = totalEvents.doubleValue() / durationSeconds;
        System.out.printf("Average EPS: %.0f%n", averageEps);

        assertEquals(totalEvents.longValue(), counter.sum());
    }

    @Tag("stress")
    @Test
    void extremeConcurrencySendAndReturnTest_progressWithMemory() throws InterruptedException {
        LongAdder counter = new LongAdder();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS);

        BigInteger totalEvents = BigInteger.valueOf(THREADS)
                .multiply(BigInteger.valueOf(EVENTS_PER_THREAD));

        long startTime = System.currentTimeMillis();

        // Monitor thread for EPS and memory
        Thread monitor = new Thread(() -> {
            long lastCount = 0;
            long lastTime = System.currentTimeMillis();
            Runtime runtime = Runtime.getRuntime();

            while (counter.sum() < totalEvents.longValue()) {
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}

                long now = System.currentTimeMillis();
                long completed = counter.sum();
                long eventsThisPeriod = completed - lastCount;
                double eps = eventsThisPeriod / ((now - lastTime) / 1000.0);

                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                double usedPercent = usedMemory * 100.0 / runtime.maxMemory();

                System.out.printf(
                        "Progress: %d/%d (%.2f%%), EPS: %.0f, Memory Used: %.2f MB (%.2f%%)%n",
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

        // Submit events asynchronously
        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                for (int i = 0; i < EVENTS_PER_THREAD; i++) {
                    var a = new EventFlow().addPostEvent(HeavyEvent.class, "payload-" + i)
                            .onResponse(HeavyEventSuccess.class, _ -> counter.increment())
                            .postEvent();

                    new EventFlow().addPostEvent(HeavyEventSuccess.class, "payload-" + i, a.getEventSnowflake())
                            .postEvent();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        double durationSeconds = (endTime - startTime) / 1000.0;

        System.out.println("Posted " + totalEvents + " events in " + durationSeconds + " seconds");
        double averageEps = totalEvents.doubleValue() / durationSeconds;
        System.out.printf("Average EPS: %.0f%n", averageEps);

        assertEquals(totalEvents.longValue(), counter.sum());
    }


    @Tag("stress")
    @Test
    void efficientExtremeConcurrencyTest() throws InterruptedException {
        final int THREADS = Runtime.getRuntime().availableProcessors();
        final int EVENTS_PER_THREAD = 5000;

        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        ConcurrentLinkedQueue<HeavyEvent> processedEvents = new ConcurrentLinkedQueue<>();

        long start = System.nanoTime();

        for (int t = 0; t < THREADS; t++) {
            executor.submit(() -> {
                for (int i = 0; i < EVENTS_PER_THREAD; i++) {
                    new EventFlow().addPostEvent(HeavyEvent.class, "payload-" + i)
                            .onResponse(HeavyEvent.class, processedEvents::add)
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

        Runtime rt = Runtime.getRuntime();
        System.out.printf("Used memory: %.2f MB%n", (rt.totalMemory() - rt.freeMemory()) / 1024.0 / 1024.0);

        assertEquals(totalEvents.intValue(), processedEvents.size());
    }

    @Tag("stress")
    @Test
    void constructorCacheVsReflection() throws Throwable {
        int iterations = 1_000_000;
        long startReflect = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            HeavyEvent.class.getDeclaredConstructors()[0].newInstance("payload", "uuid-" + i);
        }
        long endReflect = System.nanoTime();

        long startHandle = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            EventFlow a = new EventFlow().addPostEvent(HeavyEvent.class, "payload-" + i);
        }
        long endHandle = System.nanoTime();

        System.out.println("Reflection: " + (endReflect - startReflect) / 1_000_000 + " ms");
        System.out.println("MethodHandle Cache: " + (endHandle - startHandle) / 1_000_000 + " ms");
    }
}
