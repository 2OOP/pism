package org.toop.framework;

import java.net.NetworkInterface;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe, distributed unique ID generator following the Snowflake pattern.
 * <p>
 * Each generated 64-bit ID encodes:
 * <ul>
 *     <li>41-bit timestamp (milliseconds since custom epoch)</li>
 *     <li>10-bit machine identifier</li>
 *     <li>12-bit sequence number for IDs generated in the same millisecond</li>
 * </ul>
 * </p>
 *
 * <p>This implementation ensures:
 * <ul>
 *     <li>IDs are unique per machine.</li>
 *     <li>Monotonicity within the same machine.</li>
 *     <li>Safe concurrent generation via synchronized {@link #nextId()}.</li>
 * </ul>
 * </p>
 *
 * <p>Custom epoch is set to {@code 2025-01-01T00:00:00Z}.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * SnowflakeGenerator generator = new SnowflakeGenerator();
 * long id = generator.nextId();
 * }</pre>
 */
public class SnowflakeGenerator {

    /**
     * Custom epoch in milliseconds (2025-01-01T00:00:00Z).
     */
    private static final long EPOCH = Instant.parse("2025-01-01T00:00:00Z").toEpochMilli();

    // Bit allocations
    private static final long TIMESTAMP_BITS = 41;
    private static final long MACHINE_BITS = 10;
    private static final long SEQUENCE_BITS = 12;

    // Maximum values for each component
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    private static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;

    // Bit shifts for composing the ID
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS;

    /**
     * Unique machine identifier derived from network interfaces (10 bits).
     */
    private static final long machineId = SnowflakeGenerator.genMachineId();

    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private long sequence = 0L;

    /**
     * Generates a 10-bit machine identifier based on MAC addresses of network interfaces.
     * Falls back to a random value if MAC cannot be determined.
     */
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

    /**
     * For testing: manually set the last generated timestamp.
     * @param l timestamp in milliseconds
     */
    void setTime(long l) {
        this.lastTimestamp.set(l);
    }

    /**
     * Constructs a SnowflakeGenerator.
     * Validates that the machine ID is within allowed range.
     * @throws IllegalArgumentException if machine ID is invalid
     */
    public SnowflakeGenerator() {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException(
                    "Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }
    }

    /**
     * Generates the next unique ID.
     * <p>
     * If multiple IDs are generated in the same millisecond, a sequence number
     * is incremented. If the sequence overflows, waits until the next millisecond.
     * </p>
     *
     * @return a unique 64-bit ID
     * @throws IllegalStateException if clock moves backwards or timestamp exceeds 41-bit limit
     */
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

    /**
     * Waits until the next millisecond if sequence overflows.
     * @param lastTimestamp previous timestamp
     * @return new timestamp
     */
    private long waitNextMillis(long lastTimestamp) {
        long ts = timestamp();
        while (ts <= lastTimestamp) {
            ts = timestamp();
        }
        return ts;
    }

    /**
     * Returns current system timestamp in milliseconds.
     */
    private long timestamp() {
        return System.currentTimeMillis();
    }
}
