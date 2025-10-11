package org.toop.framework.audio.interfaces;

import org.toop.framework.audio.VolumeTypes;
import org.toop.framework.resource.types.AudioResource;

public interface VolumeManager {
    void setVolume(double newVolume, VolumeTypes types, AudioManager<? extends AudioResource>... ams);
    double getVolume();
    double getFxVolume();
    double getMusicVolume();
}
