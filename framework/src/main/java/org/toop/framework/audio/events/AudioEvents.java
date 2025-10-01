package org.toop.framework.audio.events;

import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

public class AudioEvents extends EventsBase {
    /** Starts playing a sound. */
    public record PlayAudio(String fileName, boolean loop)
            implements EventWithoutSnowflake {}

    public record StopAudio(long clipId) implements EventWithoutSnowflake {}
}
