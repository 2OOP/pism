package org.toop.framework.audio;

import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

public class AudioVolumeManager {
    private final SoundManager sM;

    private double volume = 1.0;
    private double fxVolume = 1.0;
    private double musicVolume = 1.0;

    public AudioVolumeManager(SoundManager soundManager) {
        this.sM = soundManager;

        new EventFlow()
                .listen(this::handleVolumeChange)
                .listen(this::handleFxVolumeChange)
                .listen(this::handleMusicVolumeChange)
                .listen(this::handleGetCurrentVolume)
                .listen(this::handleGetCurrentFxVolume)
                .listen(this::handleGetCurrentMusicVolume);
    }

    public void updateMusicVolume(MediaPlayer mediaPlayer) {
        mediaPlayer.setVolume(this.musicVolume * this.volume);
    }

    public void updateSoundEffectVolume(Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl volumeControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float dB =
                    (float)
                            (Math.log10(Math.max(this.fxVolume * this.volume, 0.0001))
                                    * 20.0); // convert linear to dB
            dB = Math.max(min, Math.min(max, dB));
            volumeControl.setValue(dB);
        }
    }

    private double limitVolume(double volume) {
        if (volume > 1.0) return 1.0;
        else return Math.max(volume, 0.0);
    }

    private void handleFxVolumeChange(AudioEvents.ChangeFxVolume event) {
        this.fxVolume = limitVolume(event.newVolume() / 100);
        for (Clip clip : sM.getActiveSoundEffects().values()) {
            updateSoundEffectVolume(clip);
        }
    }

    private void handleVolumeChange(AudioEvents.ChangeVolume event) {
        this.volume = limitVolume(event.newVolume() / 100);
        for (MediaPlayer mediaPlayer : sM.getActiveMusic()) {
            this.updateMusicVolume(mediaPlayer);
        }
        for (Clip clip : sM.getActiveSoundEffects().values()) {
            updateSoundEffectVolume(clip);
        }
    }

    private void handleMusicVolumeChange(AudioEvents.ChangeMusicVolume event) {
        this.musicVolume = limitVolume(event.newVolume() / 100);
        for (MediaPlayer mediaPlayer : sM.getActiveMusic()) {
            this.updateMusicVolume(mediaPlayer);
        }
    }

    private void handleGetCurrentVolume(AudioEvents.GetCurrentVolume event) {
        new EventFlow()
                .addPostEvent(
                        new AudioEvents.GetCurrentVolumeResponse(volume * 100, event.snowflakeId()))
                .asyncPostEvent();
    }

    private void handleGetCurrentFxVolume(AudioEvents.GetCurrentFxVolume event) {
        new EventFlow()
                .addPostEvent(
                        new AudioEvents.GetCurrentFxVolumeResponse(
                                fxVolume * 100, event.snowflakeId()))
                .asyncPostEvent();
    }

    private void handleGetCurrentMusicVolume(AudioEvents.GetCurrentMusicVolume event) {
        new EventFlow()
                .addPostEvent(
                        new AudioEvents.GetCurrentMusicVolumeResponse(
                                musicVolume * 100, event.snowflakeId()))
                .asyncPostEvent();
    }
}
