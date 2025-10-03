package org.toop.framework.audio.events;

import org.toop.framework.asset.resources.MusicAsset;
import org.toop.framework.eventbus.events.EventWithSnowflake;
import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

import java.util.Map;

public class AudioEvents extends EventsBase {
    /** Starts playing a sound. */
    public record PlayAudio(String fileName, boolean loop)
            implements EventWithoutSnowflake {}

    public record StopAudio(long clipId) implements EventWithoutSnowflake {}

    public record StartBackgroundMusic() implements EventWithoutSnowflake {}
    public record ChangeVolume(double newVolume) implements EventWithoutSnowflake {}
    public record GetCurrentVolume(long snowflakeId) implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Map.of();
        }

        @Override
        public long eventSnowflake() {
            return snowflakeId;
        }
    }
    public record GetCurrentVolumeReponse(double currentVolume, long snowflakeId) implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Map.of();
        }

        @Override
        public long eventSnowflake() {
            return snowflakeId;
        }
    }
    public record playOnClickButton() implements EventWithoutSnowflake {}
}
