package org.toop.framework.audio.events;

import org.toop.framework.audio.VolumeControl;
import org.toop.framework.eventbus.events.*;
import org.toop.framework.eventbus.events.ResponseToUniqueEvent;
import org.toop.framework.eventbus.events.UniqueEvent;

public class AudioEvents extends EventsBase {
    /** Stops the audio manager. */
    public record StopAudioManager() implements GenericEvent {}

    /** Start playing a sound effect. */
    public record PlayEffect(String fileName, boolean loop) implements GenericEvent {}

    /** Stop playing a sound effect. */
    public record StopEffect(String fileName) implements GenericEvent {}

    /** Start background music. */
    public record StartBackgroundMusic() implements GenericEvent {}

    /** Gives back the name of the song, the position its currently at (in seconds) and how long it takes (in seconds) */
    public record PlayingMusic(String name, long currentPosition, long duration) implements GenericEvent {}

    /** Skips the song to the last second of the song resulting in a skip effect */
    public record SkipMusic() implements GenericEvent {}

    /** Change volume, choose type with {@link VolumeControl}. */
    public record ChangeVolume(double newVolume, VolumeControl controlType) implements GenericEvent {}

    /** Requests the desired volume by selecting it with {@link VolumeControl}. */
    public record GetVolume(VolumeControl controlType, long identifier) implements UniqueEvent {}

    /** Response to GetVolume. */
    public record GetVolumeResponse(double currentVolume, long identifier) implements ResponseToUniqueEvent {}

    /** Plays the predetermined sound for pressing a button. */
    public record ClickButton() implements GenericEvent {}
}
