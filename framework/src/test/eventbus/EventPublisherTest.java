package org.toop.eventbus;

import org.junit.jupiter.api.Test;
import org.toop.framework.eventbus.events.EventWithUuid;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class EventPublisherTest {

    // Simple test event implementing EventWithUuid
    public record TestEvent(String name, String eventId) implements EventWithUuid {
        @Override
        public Map<String, Object> result() {
            return Map.of("name", name, "eventId", eventId);
        }
    }

    public record TestResponseEvent(String msg, String eventId) implements EventWithUuid {
        @Override
        public Map<String, Object> result() {
            return Map.of("msg", msg, "eventId", eventId);
        }
    }

    @Test
    void testEventPublisherGeneratesUuid() {
        EventPublisher<TestEvent> publisher = new EventPublisher<>(TestEvent.class, "myTest");
        assertNotNull(publisher.getEventId());
        assertEquals(publisher.getEventId(), publisher.getEvent().eventId());
    }

    @Test
    void testPostEvent() {
        AtomicBoolean triggered = new AtomicBoolean(false);

        EventPublisher<TestEvent> publisher = new EventPublisher<>(TestEvent.class, "myTest");
        publisher.onEventById(TestEvent.class, event -> triggered.set(true))
                .postEvent();

        assertTrue(triggered.get(), "Subscriber should have been triggered by postEvent");
    }

    @Test
    void testOnEventByIdMatchesUuid() {
        AtomicBoolean triggered = new AtomicBoolean(false);

        EventPublisher<TestEvent> publisher1 = new EventPublisher<>(TestEvent.class, "event1");
        EventPublisher<TestEvent> publisher2 = new EventPublisher<>(TestEvent.class, "event2");

        publisher1.onEventById(TestEvent.class, event -> triggered.set(true));
        publisher2.postEvent();

        // Only publisher1's subscriber should trigger for its UUID
        assertFalse(triggered.get(), "Subscriber should not trigger for a different UUID");

        publisher1.postEvent();
        assertTrue(triggered.get(), "Subscriber should trigger for matching UUID");
    }

    @Test
    void testUnregisterAfterSuccess() {
        AtomicBoolean triggered = new AtomicBoolean(false);
        AtomicReference<Object> listenerRef = new AtomicReference<>();

        EventPublisher<TestEvent> publisher = new EventPublisher<>(TestEvent.class, "event");
        publisher.onEventById(TestEvent.class, event -> triggered.set(true))
                .unsubscribeAfterSuccess()
                .postEvent();

        // Subscriber should have been removed after first trigger
        assertTrue(triggered.get(), "Subscriber should trigger first time");

        triggered.set(false);
        publisher.postEvent();
        assertFalse(triggered.get(), "Subscriber should not trigger after unregister");
    }

    @Test
    void testResultMapPopulated() {
        AtomicReference<Map<String, Object>> resultRef = new AtomicReference<>();

        EventPublisher<TestEvent> publisher = new EventPublisher<>(TestEvent.class, "myName");
        publisher.onEventById(TestEvent.class, event -> resultRef.set(event.result()))
                .postEvent();

        Map<String, Object> result = resultRef.get();
        assertNotNull(result);
        assertEquals("myName", result.get("name"));
        assertEquals(publisher.getEventId(), result.get("eventId"));
    }

    @Test
    void testMultipleSubscribers() {
        AtomicBoolean firstTriggered = new AtomicBoolean(false);
        AtomicBoolean secondTriggered = new AtomicBoolean(false);

        EventPublisher<TestEvent> publisher = new EventPublisher<>(TestEvent.class, "multi");

        publisher.onEventById(TestEvent.class, e -> firstTriggered.set(true))
                .onEventById(TestEvent.class, e -> secondTriggered.set(true))
                .postEvent();

        assertTrue(firstTriggered.get());
        assertTrue(secondTriggered.get());

        publisher.onEventById(TestEvent.class, e -> firstTriggered.set(true))
                .onEventById(TestEvent.class, e -> secondTriggered.set(true))
                .asyncPostEvent();

        assertTrue(firstTriggered.get());
        assertTrue(secondTriggered.get());
    }

    @Test
    void testEventInstanceCreatedCorrectly() {
        EventPublisher<TestEvent> publisher = new EventPublisher<>(TestEvent.class, "hello");
        TestEvent event = publisher.getEvent();
        assertNotNull(event);
        assertEquals("hello", event.name());
        assertEquals(publisher.getEventId(), event.eventId());
    }
}
