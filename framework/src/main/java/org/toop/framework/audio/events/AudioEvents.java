package org.toop.framework.audio.events;

import java.util.Map;
import org.toop.framework.eventbus.events.EventWithSnowflake;
import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

public class AudioEvents extends EventsBase {
    public record StopAudioManager() implements EventWithoutSnowflake {}

    /** Starts playing a sound. */
    public record PlayEffect(String fileName, boolean loop) implements EventWithoutSnowflake {}

    public record StopEffect(long clipId) implements EventWithoutSnowflake {}

    public record StartBackgroundMusic() implements EventWithoutSnowflake {}

    public record ChangeVolume(double newVolume) implements EventWithoutSnowflake {}

    public record ChangeFxVolume(double newVolume) implements EventWithoutSnowflake {}

    public record ChangeMusicVolume(double newVolume) implements EventWithoutSnowflake {}

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

    public record GetCurrentVolumeResponse(double currentVolume, long snowflakeId)
            implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Map.of();
        }

        @Override
        public long eventSnowflake() {
            return snowflakeId;
        }
    }

    public record GetCurrentFxVolume(long snowflakeId) implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Map.of();
        }

        @Override
        public long eventSnowflake() {
            return this.snowflakeId;
        }
    }

    public record GetCurrentMusicVolume(long snowflakeId) implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Map.of();
        }

        @Override
        public long eventSnowflake() {
            return this.snowflakeId;
        }
    }

    public record GetCurrentFxVolumeResponse(double currentVolume, long snowflakeId)
            implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Map.of();
        }

        @Override
        public long eventSnowflake() {
            return this.snowflakeId;
        }
    }

    public record GetCurrentMusicVolumeResponse(double currentVolume, long snowflakeId)
            implements EventWithSnowflake {
        @Override
        public Map<String, Object> result() {
            return Map.of();
        }

        @Override
        public long eventSnowflake() {
            return this.snowflakeId;
        }
    }

    public record ClickButton() implements EventWithoutSnowflake {}
}
