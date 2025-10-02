package org.toop.framework.eventbus;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.*;
import org.toop.framework.eventbus.events.EventType;
import org.toop.framework.eventbus.events.EventWithSnowflake;

class GlobalEventBusTest {

    // ------------------------------------------------------------------------
    // Test Events
    // ------------------------------------------------------------------------
    private record TestEvent(String message) implements EventType {}

    private record TestSnowflakeEvent(long eventSnowflake, String payload)
            implements EventWithSnowflake {
        @Override
        public java.util.Map<String, Object> result() {
            return java.util.Map.of("payload", payload);
        }
    }

    static class SampleEvent implements EventType {
        private final String message;

        SampleEvent(String message) {
            this.message = message;
        }

        public String message() {
            return message;
        }
    }

    @AfterEach
    void cleanup() {
        GlobalEventBus.reset();
    }

    // ------------------------------------------------------------------------
    // Subscriptions
    // ------------------------------------------------------------------------
    @Test
    void testSubscribeAndPost() {
        AtomicReference<String> received = new AtomicReference<>();
        Consumer<TestEvent> listener = e -> received.set(e.message());

        GlobalEventBus.subscribe(TestEvent.class, listener);
        GlobalEventBus.post(new TestEvent("hello"));

        assertEquals("hello", received.get());
    }

    @Test
    void testUnsubscribe() {
        GlobalEventBus.reset();

        AtomicBoolean called = new AtomicBoolean(false);

        // Subscribe and keep the wrapper reference
        Consumer<? super EventType> subscription =
                GlobalEventBus.subscribe(SampleEvent.class, e -> called.set(true));

        // Post once -> should trigger
        GlobalEventBus.post(new SampleEvent("test1"));
        assertTrue(called.get(), "Listener should be triggered before unsubscribe");

        // Reset flag
        called.set(false);

        // Unsubscribe using the wrapper reference
        GlobalEventBus.unsubscribe(subscription);

        // Post again -> should NOT trigger
        GlobalEventBus.post(new SampleEvent("test2"));
        assertFalse(called.get(), "Listener should not be triggered after unsubscribe");
    }

    @Test
    void testSubscribeGeneric() {
        AtomicReference<EventType> received = new AtomicReference<>();
        Consumer<Object> listener = e -> received.set((EventType) e);

        GlobalEventBus.subscribe(listener);
        TestEvent event = new TestEvent("generic");
        GlobalEventBus.post(event);

        assertEquals(event, received.get());
    }

    @Test
    void testSubscribeById() {
        AtomicReference<String> received = new AtomicReference<>();
        long id = 42L;

        GlobalEventBus.subscribeById(TestSnowflakeEvent.class, id, e -> received.set(e.payload()));
        GlobalEventBus.post(new TestSnowflakeEvent(id, "snowflake"));

        assertEquals("snowflake", received.get());
    }

    @Test
    void testUnsubscribeById() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        long id = 99L;

        GlobalEventBus.subscribeById(TestSnowflakeEvent.class, id, e -> triggered.set(true));
        GlobalEventBus.unsubscribeById(TestSnowflakeEvent.class, id);

        GlobalEventBus.post(new TestSnowflakeEvent(id, "ignored"));
        assertFalse(triggered.get(), "Listener should not be triggered after unsubscribeById");
    }

    // ------------------------------------------------------------------------
    // Async posting
    // ------------------------------------------------------------------------
    @Test
    void testPostAsync() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        GlobalEventBus.subscribe(
                TestEvent.class,
                e -> {
                    if ("async".equals(e.message())) {
                        latch.countDown();
                    }
                });

        GlobalEventBus.postAsync(new TestEvent("async"));

        assertTrue(
                latch.await(1, TimeUnit.SECONDS), "Async event should be received within timeout");
    }

    // ------------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------------
    @Test
    void testResetClearsListeners() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        GlobalEventBus.subscribe(TestEvent.class, e -> triggered.set(true));

        GlobalEventBus.reset();
        GlobalEventBus.post(new TestEvent("ignored"));

        assertFalse(triggered.get(), "Listener should not be triggered after reset");
    }

    @Test
    void testShutdown() {
        // Should not throw
        assertDoesNotThrow(GlobalEventBus::shutdown);
    }
}
