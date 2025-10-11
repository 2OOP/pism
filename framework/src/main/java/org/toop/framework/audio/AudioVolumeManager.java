package org.toop.framework.audio;

import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.audio.interfaces.VolumeManager;
import org.toop.framework.resource.types.AudioResource;

public class AudioVolumeManager implements VolumeManager {

    public void setVolume(double newVolume, VolumeTypes type) {
        type.setVolume(newVolume, VolumeTypes.MASTERVOLUME.getVolume());
    }

    public double getVolume(VolumeTypes type) {
        return type.getVolume();
    }

    public AudioVolumeManager registerManager(VolumeTypes type, AudioManager<? extends AudioResource> manager) {
        if (manager != null) {
            type.addManager(manager);
        }
        return AudioVolumeManager.this;
    }

    @Override
    public void updateAllVolumes() {
        double masterVolume = VolumeTypes.MASTERVOLUME.getVolume();

        for (VolumeTypes type : VolumeTypes.values()) {
            if (type != VolumeTypes.MASTERVOLUME) { // skip master itself
                type.setVolume(type.getVolume(), masterVolume);
            }
        }
    }

}