import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.eventbus.EventMeta;
import org.toop.eventbus.EventRegistry;
import org.toop.eventbus.GlobalEventBus;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

public class GlobalEventBusTest {

    // Sample event class
    public static class TestEvent {
        private final String message;

        public TestEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }

    @BeforeEach
    public void resetRegistry() {
        EventRegistry.reset(); // clear ready states and stored events
    }

    @Test
    public void testSubscribeAndPost() {
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        // Subscribe and register listener
        EventMeta<TestEvent> meta = GlobalEventBus.subscribeAndRegister(TestEvent.class, e -> {
            called.set(true);
            receivedMessage.set(e.getMessage());
        });

        assertTrue(EventRegistry.isReady(TestEvent.class));
        assertTrue(meta.isReady());

        // Post an event
        TestEvent event = new TestEvent("Hello World");
        GlobalEventBus.post(event);

        // Give Guava EventBus a moment (optional if single-threaded)
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        assertTrue(called.get());
        assertEquals("Hello World", receivedMessage.get());
    }

    @Test
    public void testUnregister() {
        AtomicBoolean called = new AtomicBoolean(false);

        EventMeta<TestEvent> meta = GlobalEventBus.subscribeAndRegister(TestEvent.class, e -> called.set(true));
        assertTrue(meta.isReady());
        assertTrue(EventRegistry.isReady(TestEvent.class));

        // Unregister listener
        GlobalEventBus.unregister(meta);

        assertFalse(meta.isReady());
        assertFalse(EventRegistry.isReady(TestEvent.class));

        // Post event — listener should NOT be called
        GlobalEventBus.post(new TestEvent("Test"));
        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        assertFalse(called.get());
    }

    @Test
    public void testMultipleListeners() {
        AtomicBoolean listener1Called = new AtomicBoolean(false);
        AtomicBoolean listener2Called = new AtomicBoolean(false);

        EventMeta<TestEvent> l1 = GlobalEventBus.subscribeAndRegister(TestEvent.class, e -> listener1Called.set(true));
        EventMeta<TestEvent> l2 = GlobalEventBus.subscribeAndRegister(TestEvent.class, e -> listener2Called.set(true));

        GlobalEventBus.post(new TestEvent("Event"));

        try { Thread.sleep(50); } catch (InterruptedException ignored) {}

        assertTrue(listener1Called.get());
        assertTrue(listener2Called.get());
    }

    // TODO: Fix registry
//    @Test
//    public void testEventStoredInRegistry() {
//        // Subscribe listener (marks type ready)
//        EventMeta<TestEvent> meta = GlobalEventBus.subscribeAndRegister(TestEvent.class, e -> {});
//
//        // Post the event
//        TestEvent event = new TestEvent("StoreTest");
//        GlobalEventBus.post(event);
//
//        // Retrieve the last stored EventEntry
//        EventRegistry.EventEntry<TestEvent> storedEntry = EventRegistry.getLastEvent(TestEvent.class);
//
//        assertNotNull(storedEntry);
//
//        // Compare the inner event
//        TestEvent storedEvent = storedEntry.getEvent();
//        assertEquals(event, storedEvent);
//    }
}