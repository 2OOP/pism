package org.toop.framework.eventbus;

import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeGenerator {
    // Epoch start (choose your custom epoch to reduce bits wasted on old time)
    private static final long EPOCH = 1700000000000L; // ~2023-11-15

    // Bit allocations
    private static final long TIMESTAMP_BITS = 41;
    private static final long MACHINE_BITS = 10;
    private static final long SEQUENCE_BITS = 12;

    // Max values
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Bit shifts
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS;

    private final long machineId;
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private long sequence = 0L;

    public SnowflakeGenerator(long machineId) {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp.get()) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate id.");
        }

        if (currentTimestamp == lastTimestamp.get()) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence overflow, wait for next millisecond
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp.set(currentTimestamp);

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long ts = timestamp();
        while (ts <= lastTimestamp) {
            ts = timestamp();
        }
        return ts;
    }

    private long timestamp() {
        return System.currentTimeMillis();
    }
}