package org.toop.framework;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SnowflakeGeneratorTest {

    @Test
    void testMachineIdWithinBounds() {
        SnowflakeGenerator generator = new SnowflakeGenerator();
        long machineIdField = getMachineId(generator);
        assertTrue(
                machineIdField >= 0 && machineIdField <= 1023,
                "Machine ID should be within 0-1023");
    }

    @Test
    void testNextIdReturnsUniqueValues() {
        SnowflakeGenerator generator = new SnowflakeGenerator();
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            long id = generator.nextId();
            assertFalse(ids.contains(id), "Duplicate ID generated");
            ids.add(id);
        }
    }

    @Test
    void testSequenceRollover() throws Exception {
        SnowflakeGenerator generator =
                new SnowflakeGenerator() {
                    private long fakeTime = System.currentTimeMillis();

                    protected long timestamp() {
                        return fakeTime;
                    }

                    void incrementTime() {
                        fakeTime++;
                    }
                };

        long first = generator.nextId();
        long second = generator.nextId();
        assertNotEquals(
                first, second, "IDs generated within same millisecond should differ by sequence");

        // Force sequence overflow
        for (int i = 0; i < (1 << 12); i++) generator.nextId();
        long afterOverflow = generator.nextId();
        assertTrue(afterOverflow > second, "ID after sequence rollover should be greater");
    }

    @Test
    void testNextIdMonotonic() {
        SnowflakeGenerator generator = new SnowflakeGenerator();
        long prev = generator.nextId();
        for (int i = 0; i < 100; i++) {
            long next = generator.nextId();
            assertTrue(next > prev, "IDs must be increasing");
            prev = next;
        }
    }

    // Helper: reflectively get machineId
    private long getMachineId(SnowflakeGenerator generator) {
        try {
            var field = SnowflakeGenerator.class.getDeclaredField("machineId");
            field.setAccessible(true);
            return (long) field.get(generator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
