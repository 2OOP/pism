package org.toop.framework.audio.events;

import org.toop.framework.asset.resources.MusicAsset;
import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

public class AudioEvents extends EventsBase {
    /** Starts playing a sound. */
    public record PlayAudio(String fileName, boolean loop)
            implements EventWithoutSnowflake {}

    public record StopAudio(long clipId) implements EventWithoutSnowflake {}

    public record StartBackgroundMusic() implements EventWithoutSnowflake {}
    public record ChangeVolume(double newVolume) implements EventWithoutSnowflake {}
    public record playOnClickButton() implements EventWithoutSnowflake {}
}
