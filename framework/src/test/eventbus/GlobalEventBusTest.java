//package org.toop.eventbus;
//
//import net.engio.mbassy.bus.publication.SyncAsyncPostCommand;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.toop.eventbus.events.IEvent;
//
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class GlobalEventBusTest {
//
//    // A simple test event
//    static class TestEvent implements IEvent {
//        private final String message;
//
//        TestEvent(String message) {
//            this.message = message;
//        }
//
//        String getMessage() {
//            return message;
//        }
//    }
//
//    @AfterEach
//    void tearDown() {
//        // Reset to avoid leaking subscribers between tests
//        GlobalEventBus.reset();
//    }
//
//    @Test
//    void testSubscribeWithType() {
//        AtomicReference<String> result = new AtomicReference<>();
//
//        GlobalEventBus.subscribe(TestEvent.class, e -> result.set(e.getMessage()));
//
//        GlobalEventBus.post(new TestEvent("hello"));
//
//        assertEquals("hello", result.get());
//    }
//
//    @Test
//    void testSubscribeWithoutType() {
//        AtomicReference<String> result = new AtomicReference<>();
//
//        GlobalEventBus.subscribe((TestEvent e) -> result.set(e.getMessage()));
//
//        GlobalEventBus.post(new TestEvent("world"));
//
//        assertEquals("world", result.get());
//    }
//
//    @Test
//    void testUnsubscribeStopsReceivingEvents() {
//        AtomicBoolean called = new AtomicBoolean(false);
//
//        Object listener = GlobalEventBus.subscribe(TestEvent.class, e -> called.set(true));
//
//        // First event should trigger
//        GlobalEventBus.post(new TestEvent("first"));
//        assertTrue(called.get());
//
//        // Reset flag
//        called.set(false);
//
//        // Unsubscribe and post again
//        GlobalEventBus.unsubscribe(listener);
//        GlobalEventBus.post(new TestEvent("second"));
//
//        assertFalse(called.get(), "Listener should not be called after unsubscribe");
//    }
//
//    @Test
//    void testResetClearsListeners() {
//        AtomicBoolean called = new AtomicBoolean(false);
//
//        GlobalEventBus.subscribe(TestEvent.class, e -> called.set(true));
//
//        GlobalEventBus.reset(); // should wipe subscriptions
//
//        GlobalEventBus.post(new TestEvent("ignored"));
//
//        assertFalse(called.get(), "Listener should not survive reset()");
//    }

//    @Test
//    void testSetReplacesBus() {
//        MBassadorMock<IEvent> mockBus = new MBassadorMock<>();
//        GlobalEventBus.set(mockBus);
//
//        TestEvent event = new TestEvent("test");
//        GlobalEventBus.post(event);
//
//        assertEquals(event, mockBus.lastPosted, "Custom bus should receive the event");
//    }
//
//    // Minimal fake MBassador for verifying set()
//    static class MBassadorMock<T extends IEvent> extends net.engio.mbassy.bus.MBassador<T> {
//        T lastPosted;
//
//        @Override
//        public SyncAsyncPostCommand<T> post(T message) {
//            this.lastPosted = message;
//            return super.post(message);
//        }
//    }
//}
