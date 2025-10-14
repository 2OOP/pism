package org.toop.framework;

import java.net.NetworkInterface;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe, distributed unique ID generator following the Snowflake pattern.
 *
 * <p>Each generated 64-bit ID encodes:
 * <ul>
 *   <li>41-bit timestamp (milliseconds since custom epoch)
 *   <li>10-bit machine identifier
 *   <li>12-bit sequence number for IDs generated in the same millisecond
 * </ul>
 *
 * <p>This static implementation ensures global uniqueness per JVM process
 * and can be accessed via {@link SnowflakeGenerator#nextId()}.
 */
public final class SnowflakeGenerator {

    /** Custom epoch in milliseconds (2025-01-01T00:00:00Z). */
    private static final long EPOCH = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli();

    // Bit allocations
    private static final long TIMESTAMP_BITS = 41;
    private static final long MACHINE_BITS = 10;
    private static final long SEQUENCE_BITS = 12;

    // Maximum values
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    private static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;

    // Bit shifts
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS;

    /** Unique machine identifier derived from MAC addresses. */
    private static final long MACHINE_ID = genMachineId();

    /** State variables (shared across all threads). */
    private static final AtomicLong LAST_TIMESTAMP = new AtomicLong(-1L);
    private static long sequence = 0L;

    // Prevent instantiation
    private SnowflakeGenerator() {}

    /** Generates a 10-bit machine identifier from MAC or random fallback. */
    private static long genMachineId() {
        try {
            StringBuilder sb = new StringBuilder();
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) sb.append(String.format("%02X", b));
                }
            }
            return sb.toString().hashCode() & 0x3FF; // limit to 10 bits
        } catch (Exception e) {
            return (long) (Math.random() * 1024); // fallback
        }
    }

    /** Returns a globally unique 64-bit Snowflake ID. */
    public static synchronized long nextId() {
        long currentTimestamp = timestamp();

        if (currentTimestamp < LAST_TIMESTAMP.get()) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate ID.");
        }

        if (currentTimestamp > MAX_TIMESTAMP) {
            throw new IllegalStateException("Timestamp bits overflow — Snowflake expired.");
        }

        if (currentTimestamp == LAST_TIMESTAMP.get()) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L;
        }

        LAST_TIMESTAMP.set(currentTimestamp);

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (MACHINE_ID << MACHINE_SHIFT)
                | sequence;
    }

    /** Waits until next millisecond if sequence exhausted. */
    private static long waitNextMillis(long lastTimestamp) {
        long ts = timestamp();
        while (ts <= lastTimestamp) ts = timestamp();
        return ts;
    }

    /** Returns current timestamp in milliseconds. */
    private static long timestamp() {
        return System.currentTimeMillis();
    }
}
