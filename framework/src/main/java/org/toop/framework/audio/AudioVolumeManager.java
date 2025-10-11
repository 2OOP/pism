package org.toop.framework.audio;

import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.audio.interfaces.VolumeManager;

public class AudioVolumeManager implements VolumeManager {
    private double volume = 0.0;
    private double fxVolume = 0.0;
    private double musicVolume = 0.0;

    public AudioVolumeManager() {}

    private <T extends MediaPlayer> void updateMusicVolume(T mediaPlayer) {
        mediaPlayer.setVolume(this.musicVolume * this.volume);
    }

    private <T extends Clip> void updateSoundEffectVolume(T clip) {
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

    @Override
    public void setVolume(double newVolume, AudioManager<?> sm, AudioManager<?> mm) {
        this.volume = limitVolume(newVolume / 100);
        for (var clip : sm.getActiveAudio()) {
            this.updateSoundEffectVolume((Clip) clip);
        }
        for (var mediaPlayer : mm.getActiveAudio()) {
            this.updateMusicVolume((MediaPlayer) mediaPlayer);
        }
    }

    @Override
    public void setFxVolume(double newVolume, AudioManager<?> sm) {
        this.fxVolume = limitVolume(newVolume / 100);
        for (var clip : sm.getActiveAudio()) {
            this.updateSoundEffectVolume((Clip) clip); // TODO: What if not clip
        }
    }

    @Override
    public void setMusicVolume(double newVolume, AudioManager<?> mm) {
        this.musicVolume = limitVolume(newVolume / 100);
        for (var mediaPlayer : mm.getActiveAudio()) {
            this.updateMusicVolume((MediaPlayer) mediaPlayer); // TODO; What if not MediaPlayer
        }
    }

    @Override
    public double getVolume() {
        return volume * 100;
    }

    @Override
    public double getFxVolume() {
        return fxVolume * 100;
    }

    @Override
    public double getMusicVolume() {
        return musicVolume * 100;
    }
}
