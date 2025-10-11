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
                .listen(this::handleFxVolumeChange)
                .listen(this::handleMusicVolumeChange)
                .listen(this::handleGetVolume)
                .listen(this::handleGetFxVolume)
                .listen(this::handleGetMusicVolume)
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
        this.audioVolumeManager.setVolume(event.newVolume() / 100, VolumeTypes.MASTERVOLUME);
        this.audioVolumeManager.updateAllVolumes();
    }

    private void handleFxVolumeChange(AudioEvents.ChangeFxVolume event) {
        this.audioVolumeManager.setVolume(event.newVolume() / 100, VolumeTypes.FX);
        this.audioVolumeManager.updateAllVolumes();
    }

    private void handleMusicVolumeChange(AudioEvents.ChangeMusicVolume event) {
        this.audioVolumeManager.setVolume(event.newVolume() / 100, VolumeTypes.MUSIC);
        this.audioVolumeManager.updateAllVolumes();
    }

    private void handleGetVolume(AudioEvents.GetCurrentVolume event) {
        new EventFlow()
            .addPostEvent(
                    new AudioEvents.GetCurrentVolumeResponse(
                            audioVolumeManager.getVolume(VolumeTypes.MASTERVOLUME),
                            event.snowflakeId()))
            .asyncPostEvent();
    }

    private void handleGetFxVolume(AudioEvents.GetCurrentFxVolume event) {
        new EventFlow()
            .addPostEvent(
                    new AudioEvents.GetCurrentFxVolumeResponse(
                            audioVolumeManager.getVolume(VolumeTypes.FX),
                            event.snowflakeId()))
            .asyncPostEvent();
    }

    private void handleGetMusicVolume(AudioEvents.GetCurrentMusicVolume event) {
        new EventFlow()
            .addPostEvent(
                    new AudioEvents.GetCurrentMusicVolumeResponse(
                            audioVolumeManager.getVolume(VolumeTypes.MUSIC),
                            event.snowflakeId()))
            .asyncPostEvent();
    }

}
