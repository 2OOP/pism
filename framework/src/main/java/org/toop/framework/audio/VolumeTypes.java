package org.toop.framework.audio;

import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.resource.types.AudioResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public enum VolumeTypes {
    VOLUME(),
    FX(),
    MUSIC();

    private final List<AudioManager<? extends AudioResource>> managers = new ArrayList<>();
    private double volume = 1.0;
    private double masterVolume = 1.0;

    public void setVolume(double newVolume, double currentMasterVolume) {
        this.volume = clamp(newVolume);

        if (this != VOLUME) {
            this.masterVolume = clamp(currentMasterVolume);
        }

        double effectiveVolume = computeEffectiveVolume();
        broadcastVolume(effectiveVolume);
    }

    private double computeEffectiveVolume() {
        return (this == VOLUME) ? volume : volume * masterVolume;
    }

    private void broadcastVolume(double effectiveVolume) {
        managers.stream()
                .filter(Objects::nonNull)
                .forEach(manager -> manager.getActiveAudio()
                .forEach(aud -> aud.updateVolume(effectiveVolume)));
    }

    private double clamp(double vol) {
        return Math.max(0, Math.min(vol, 1.0));
    }

    public double getVolume() {
        return volume;
    }

    public void addManager(AudioManager<? extends AudioResource> manager) {
        if (manager != null && !managers.contains(manager)) {
            managers.add(manager);
        }
    }

    public void removeManager(AudioManager<? extends AudioResource> manager) {
        if (manager != null) {
            managers.remove(manager);
        }
    }

    public List<AudioManager<? extends AudioResource>> getManagers() {
        return Collections.unmodifiableList(managers);
    }
}