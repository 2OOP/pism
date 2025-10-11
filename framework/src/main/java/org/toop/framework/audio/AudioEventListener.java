package org.toop.framework.audio;

import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.audio.interfaces.MusicManager;
import org.toop.framework.audio.interfaces.SoundEffectManager;
import org.toop.framework.audio.interfaces.VolumeManager;
import org.toop.framework.eventbus.EventFlow;

public class AudioEventListener {
    private final MusicManager<?> musicManager;
    private final SoundEffectManager<?> soundEffectManager;
    private final VolumeManager audioVolumeManager;

    public AudioEventListener(
            MusicManager<?> musicManager,
            SoundEffectManager<?> soundEffectManager,
            VolumeManager audioVolumeManager
    ) {
        this.musicManager = musicManager;
        this.soundEffectManager = soundEffectManager;
        this.audioVolumeManager = audioVolumeManager;
    }

    public void initListeners() {
        new EventFlow()
                .listen(this::handlePlaySound)
                .listen(this::handleStopSound)
                .listen(this::handleMusicStart)
                .listen(this::handleVolumeChange)
                .listen(this::handleFxVolumeChange)
                .listen(this::handleMusicVolumeChange)
                .listen(this::handleGetVolume)
                .listen(this::handleGetFxVolume)
                .listen(this::handleGetMusicVolume);
    }

    private void handlePlaySound(AudioEvents.PlayEffect event) {
        this.soundEffectManager.play(event.fileName(), event.loop());
    }

    private void handleStopSound(AudioEvents.StopEffect event) {
        this.soundEffectManager.stop(event.clipId());
    }

    private void handleMusicStart(AudioEvents.StartBackgroundMusic event) {
        this.musicManager.play();
    }

    private void handleVolumeChange(AudioEvents.ChangeVolume event) {
        this.audioVolumeManager.setVolume(event.newVolume(), soundEffectManager, musicManager);
    }

    private void handleFxVolumeChange(AudioEvents.ChangeFxVolume event) {
        this.audioVolumeManager.setFxVolume(event.newVolume(), soundEffectManager);
    }

    private void handleMusicVolumeChange(AudioEvents.ChangeMusicVolume event) {
        this.audioVolumeManager.setMusicVolume(event.newVolume(), musicManager);
    }

    private void handleGetVolume(AudioEvents.GetCurrentVolume event) {
        new EventFlow()
            .addPostEvent(
                    new AudioEvents.GetCurrentVolumeResponse(
                            audioVolumeManager.getVolume(),
                            event.snowflakeId()))
            .asyncPostEvent();
    }

    private void handleGetFxVolume(AudioEvents.GetCurrentFxVolume event) {
        new EventFlow()
            .addPostEvent(
                    new AudioEvents.GetCurrentFxVolumeResponse(
                            audioVolumeManager.getFxVolume(),
                            event.snowflakeId()))
            .asyncPostEvent();
    }

    private void handleGetMusicVolume(AudioEvents.GetCurrentMusicVolume event) {
        new EventFlow()
            .addPostEvent(
                    new AudioEvents.GetCurrentMusicVolumeResponse(
                            audioVolumeManager.getMusicVolume(),
                            event.snowflakeId()))
            .asyncPostEvent();
    }

}
