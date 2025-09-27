package org.toop.framework;

import java.net.NetworkInterface;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

public class SnowflakeGenerator {
    private static final long EPOCH = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli();

    // Bit allocations
    private static final long TIMESTAMP_BITS = 41;
    private static final long MACHINE_BITS = 10;
    private static final long SEQUENCE_BITS = 12;

    // Max values
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    private static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;

    // Bit shifts
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS;

    private static final long machineId = SnowflakeGenerator.genMachineId();
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private long sequence = 0L;

    private static long genMachineId() {
        try {
            StringBuilder sb = new StringBuilder();
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) sb.append(String.format("%02X", b));
                }
            }
            // limit to 10 bits (0–1023)
            return sb.toString().hashCode() & 0x3FF;
        } catch (Exception e) {
            return (long) (Math.random() * 1024); // fallback
        }
    }

    public SnowflakeGenerator() {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
    }

    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < lastTimestamp.get()) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate id.");
        }

        if (currentTimestamp > MAX_TIMESTAMP) {
            throw new IllegalStateException("Timestamp bits overflow, Snowflake expired.");
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