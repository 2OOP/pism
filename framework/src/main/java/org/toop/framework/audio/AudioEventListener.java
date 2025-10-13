package org.toop.framework.audio;

import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.audio.interfaces.MusicManager;
import org.toop.framework.audio.interfaces.SoundEffectManager;
import org.toop.framework.audio.interfaces.VolumeManager;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.types.AudioResource;

public class AudioEventListener<T extends AudioResource, K extends AudioResource> {
    private final MusicManager<T> musicManager;
    private final SoundEffectManager<K> soundEffectManager;
    private final VolumeManager audioVolumeManager;

    public AudioEventListener(
            MusicManager<T> musicManager,
            SoundEffectManager<K> soundEffectManager,
            VolumeManager audioVolumeManager
    ) {
        this.musicManager = musicManager;
        this.soundEffectManager = soundEffectManager;
        this.audioVolumeManager = audioVolumeManager;
    }

    public AudioEventListener<?, ?> initListeners() {
        new EventFlow()
                .listen(this::handleStopMusicManager)
                .listen(this::handlePlaySound)
                .listen(this::handleStopSound)
                .listen(this::handleMusicStart)
                .listen(this::handleVolumeChange)
                .listen(this::handleGetVolume)
                .listen(AudioEvents.ClickButton.class, _ ->
                        soundEffectManager.play("medium-button-click.wav", false));

        return this;
    }

    private void handleStopMusicManager(AudioEvents.StopAudioManager event) {
        this.musicManager.stop();
    }

    private void handlePlaySound(AudioEvents.PlayEffect event) {
        this.soundEffectManager.play(event.fileName(), event.loop());
    }

    private void handleStopSound(AudioEvents.StopEffect event) {
        this.soundEffectManager.stop(event.fileName());
    }

    private void handleMusicStart(AudioEvents.StartBackgroundMusic event) {
        this.musicManager.play();
    }

    private void handleVolumeChange(AudioEvents.ChangeVolume event) {
        this.audioVolumeManager.setVolume(event.newVolume() / 100, event.controlType());
    }

    private void handleGetVolume(AudioEvents.GetVolume event) {
        new EventFlow()
            .addPostEvent(
                    new AudioEvents.GetVolumeResponse(
                            audioVolumeManager.getVolume(event.controlType()),
                            event.identifier()))
            .asyncPostEvent();
    }

}
