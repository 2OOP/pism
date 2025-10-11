package org.toop.framework.audio;

import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.audio.interfaces.VolumeManager;
import org.toop.framework.resource.types.AudioResource;

public class AudioVolumeManager implements VolumeManager {
    private double volume = 0.0;
    private double fxVolume = 0.0;
    private double musicVolume = 0.0;

    public AudioVolumeManager() {}

    private <T extends AudioResource> void updateVolume(T resource, double level) {
        resource.updateVolume(level);
    }

    private double limitVolume(double volume) {
        return Math.min(1.0, Math.max(0.0, volume / 100));
    }

    @Override
    public <T extends AudioResource, K extends AudioResource> void setVolume(
            double newVolume, AudioManager<T> sm, AudioManager<K> mm) {
        this.volume = limitVolume(newVolume);
        for (T clip : sm.getActiveAudio()) {
            this.updateVolume(clip, fxVolume * volume);
        }
        for (K mediaPlayer : mm.getActiveAudio()) {
            this.updateVolume(mediaPlayer, musicVolume * volume);
        }
    }

    @Override
    public <T extends AudioResource> void setFxVolume(double newVolume, AudioManager<T> sm) {
        this.fxVolume = limitVolume(newVolume);
        for (T clip : sm.getActiveAudio()) {
            this.updateVolume(clip, fxVolume * volume);
        }
    }

    @Override
    public <T extends AudioResource> void setMusicVolume(double newVolume, AudioManager<T> mm) {
        this.musicVolume = limitVolume(newVolume);
        for (T mediaPlayer : mm.getActiveAudio()) {
            this.updateVolume(mediaPlayer, musicVolume * volume);
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
