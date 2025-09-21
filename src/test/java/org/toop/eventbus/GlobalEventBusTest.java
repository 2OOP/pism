package org.toop.eventbus;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.eventbus.EventBus;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.*;

class GlobalEventBusTest {

    @BeforeEach
    void setup() {
        // Reset the singleton before each test
        GlobalEventBus.reset();
    }

    @AfterEach
    void teardown() {
        // Ensure reset after tests
        GlobalEventBus.reset();
    }

    @Test
    void testGet_returnsEventBus() {
        EventBus bus = GlobalEventBus.get();
        assertNotNull(bus, "EventBus should not be null");
        assertEquals("global-bus", bus.identifier(), "EventBus name should match");
    }

    @Test
    void testSet_replacesEventBus() {
        EventBus newBus = new EventBus("new-bus");
        GlobalEventBus.set(newBus);

        assertEquals(newBus, GlobalEventBus.get(), "EventBus should be replaced");
    }

    @Test
    void testSubscribe_wrapsConsumerAndHandlesEvent() {
        AtomicBoolean called = new AtomicBoolean(false);

        var listener = GlobalEventBus.subscribe(String.class, _ -> called.set(true));
        GlobalEventBus.register(listener);

        GlobalEventBus.post("hello");

        assertTrue(called.get(), "Consumer should have been called");
    }

    @Test
    void testSubscribeAndRegister_registersListenerAutomatically() {
        AtomicBoolean called = new AtomicBoolean(false);

        GlobalEventBus.subscribeAndRegister(String.class, _ -> called.set(true));
        GlobalEventBus.post("test-event");

        assertTrue(called.get(), "Consumer should have been called");
    }

    @Test
    void testUnregister_removesListener() {
        AtomicBoolean called = new AtomicBoolean(false);

        var listener = GlobalEventBus.subscribe(String.class, _ -> called.set(true));
        GlobalEventBus.register(listener);
        GlobalEventBus.unregister(listener);

        GlobalEventBus.post("hello");
        assertFalse(called.get(), "Consumer should not be called after unregister");
    }

    //    @Test
    //    void testPost_storesEventInRegistry() {
    //        // Simple EventMeta check
    //        class MyEvent {}
    //
    //        MyEvent event = new MyEvent();
    //        GlobalEventBus.post(event);
    //
    //        EventMeta<MyEvent> stored = EventRegistry.getStoredEvent(MyEvent.class);
    //        assertNotNull(stored, "EventMeta should be stored");
    //        assertEquals(event, stored.event(), "Stored event should match the posted one");
    //    }
}
