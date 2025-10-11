package org.toop.framework.audio.interfaces;

public interface VolumeManager {
    void setVolume(double newVolume, AudioManager<?> sm, AudioManager<?> mm);
    void setFxVolume(double newVolume, AudioManager<?> sm);
    void setMusicVolume(double newVolume, AudioManager<?> mm);
    double getVolume();
    double getFxVolume();
    double getMusicVolume();
}
