package org.toop.framework.audio.interfaces;

import org.toop.framework.audio.VolumeTypes;
import org.toop.framework.resource.types.AudioResource;

public interface VolumeManager {
    void setVolume(double newVolume, VolumeTypes type);
    double getVolume(VolumeTypes type);
    void updateAllVolumes();
}
