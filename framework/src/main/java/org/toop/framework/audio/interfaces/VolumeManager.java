package org.toop.framework.audio.interfaces;

import org.toop.framework.resource.types.AudioResource;

public interface VolumeManager {
    <T extends AudioResource, K extends AudioResource> void setVolume(double newVolume, AudioManager<T> sm, AudioManager<K> mm);
    <T extends AudioResource> void setFxVolume(double newVolume, AudioManager<T> sm);
    <T extends AudioResource> void setMusicVolume(double newVolume, AudioManager<T> mm);
    double getVolume();
    double getFxVolume();
    double getMusicVolume();
}
