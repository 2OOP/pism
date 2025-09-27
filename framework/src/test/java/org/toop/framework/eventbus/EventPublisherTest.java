package org.toop.framework.eventbus;

import org.junit.jupiter.api.Test;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.events.EventWithSnowflake;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class EventFlowTest {

    @Test
    void testSnowflakeStructure() {
        long id = new SnowflakeGenerator().nextId();

        long timestampPart = id >>> 22;
        long randomPart = id & ((1L << 22) - 1);

        assertTrue(timestampPart > 0, "Timestamp part should be non-zero");
        assertTrue(randomPart >= 0 && randomPart < (1L << 22), "Random part should be within 22 bits");
    }

    @Test
    void testSnowflakeMonotonicity() throws InterruptedException {
        SnowflakeGenerator sf = new SnowflakeGenerator(1);
        long id1 = sf.nextId();
        Thread.sleep(1); // ensure timestamp increases
        long id2 = sf.nextId();

        assertTrue(id2 > id1, "Later snowflake should be greater than earlier one");
    }

    @Test
    void testSnowflakeUniqueness() {
        SnowflakeGenerator sf = new SnowflakeGenerator(1);
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 100_000; i++) {
            long id = sf.nextId();
            assertTrue(ids.add(id), "Snowflake IDs should be unique, but duplicate found");
        }
    }

    // --- Dummy Event classes for testing ---
    static class DummySnowflakeEvent implements EventWithSnowflake {
        private final long snowflake;
        DummySnowflakeEvent(long snowflake) { this.snowflake = snowflake; }
        @Override public long eventSnowflake() { return snowflake; }
        @Override public java.util.Map<String, Object> result() { return java.util.Collections.emptyMap(); }
    }

    @Test
    void testSnowflakeIsInjectedIntoEvent() {
        EventFlow flow = new EventFlow();
        flow.addPostEvent(DummySnowflakeEvent.class); // no args, should auto-generate

        long id = flow.getEventSnowflake();
        assertNotEquals(-1, id, "Snowflake should be auto-generated");
        assertTrue(flow.getEvent() instanceof DummySnowflakeEvent);
        assertEquals(id, ((DummySnowflakeEvent) flow.getEvent()).eventSnowflake());
    }

    @Test
    void testOnResponseFiltersBySnowflake() {
        EventFlow flow = new EventFlow();
        flow.addPostEvent(DummySnowflakeEvent.class);

        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        flow.onResponse(DummySnowflakeEvent.class, event -> handlerCalled.set(true));

        // Post with non-matching snowflake
        GlobalEventBus.post(new DummySnowflakeEvent(12345L));
        assertFalse(handlerCalled.get(), "Handler should not fire for mismatched snowflake");

        // Post with matching snowflake
        GlobalEventBus.post(new DummySnowflakeEvent(flow.getEventSnowflake()));
        assertTrue(handlerCalled.get(), "Handler should fire for matching snowflake");
    }
}