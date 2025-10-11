package org.toop.framework.audio;

import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.audio.interfaces.VolumeManager;
import org.toop.framework.resource.types.AudioResource;

import java.util.Arrays;
import java.util.Objects;

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

    @SafeVarargs
    @Override
    public final void setVolume(double newVolume, VolumeTypes type, AudioManager<? extends AudioResource>... managers) {
        double limitedVolume = limitVolume(newVolume);

        switch (type) {
            case FX -> fxVolume = limitedVolume;
            case MUSIC -> musicVolume = limitedVolume;
            default -> volume = limitedVolume;
        }

        double effectiveVolume = switch (type) {
            case FX -> fxVolume * volume;
            case MUSIC -> musicVolume * volume;
            default -> volume;
        };

        Arrays.stream(managers)
                .filter(Objects::nonNull)
                .forEach(manager ->
                        manager.getActiveAudio().forEach(aud -> updateVolume(aud, effectiveVolume))
                );
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
