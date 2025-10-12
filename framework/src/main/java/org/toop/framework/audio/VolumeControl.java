package org.toop.framework.audio;

import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.resource.types.AudioResource;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Enum representing different categories of audio volume in the application.
 * <p>
 * Each volume type maintains its own volume level and a list of {@link AudioManager}s
 * that manage audio resources of that type. The enum provides methods to set, get,
 * and propagate volume changes, including master volume adjustments that automatically
 * update dependent volume types (FX and MUSIC).
 * </p>
 *
 * <p>Volume types:</p>
 * <ul>
 *     <li>{@link #MASTERVOLUME}: The global/master volume that scales all other volume types.</li>
 *     <li>{@link #FX}: Volume for sound effects, scaled by the master volume.</li>
 *     <li>{@link #MUSIC}: Volume for music tracks, scaled by the master volume.</li>
 * </ul>
 *
 * <p>Key features:</p>
 * <ul>
 *     <li>Thread-safe management of audio managers using {@link CopyOnWriteArrayList}.</li>
 *     <li>Automatic propagation of master volume changes to dependent volume types.</li>
 *     <li>Clamping volume values between 0.0 and 1.0 to ensure valid audio levels.</li>
 *     <li>Dynamic registration and removal of audio managers for each volume type.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Add a music manager to the MUSIC volume type
 * VolumeControl.MUSIC.addManager(musicManager);
 *
 * // Set master volume to 80%
 * VolumeControl.MASTERVOLUME.setVolume(0.8, 0);
 *
 * // Set FX volume to 50% of master
 * VolumeControl.FX.setVolume(0.5, VolumeControl.MASTERVOLUME.getVolume());
 *
 * // Retrieve current music volume
 * double musicVol = VolumeControl.MUSIC.getVolume();
 * }</pre>
 */
public enum VolumeControl {
    MASTERVOLUME(),
    FX(),
    MUSIC();

    @SuppressWarnings("ImmutableEnumChecker")
    private final List<AudioManager<? extends AudioResource>> managers = new CopyOnWriteArrayList<>();
    @SuppressWarnings("ImmutableEnumChecker")
    private double volume = 1.0;
    @SuppressWarnings("ImmutableEnumChecker")
    private double masterVolume = 1.0;

    /**
     * Sets the volume for this volume type.
     * <p>
     * If this type is {@link #MASTERVOLUME}, all dependent volume types
     * (FX, MUSIC, etc.) are automatically updated to reflect the new master volume.
     * Otherwise, the volume is scaled by the provided master volume.
     *
     * @param newVolume the new volume level (0.0 to 1.0)
     * @param currentMasterVolume the current master volume for scaling non-master types
     */
    public void setVolume(double newVolume, double currentMasterVolume) {
        this.volume = clamp(newVolume);

        if (this == MASTERVOLUME) {
            for (VolumeControl type : VolumeControl.values()) {
                if (type != MASTERVOLUME) {
                    type.masterVolume = this.volume;
                    type.broadcastVolume(type.computeEffectiveVolume());
                }
            }
        } else {
            this.masterVolume = clamp(currentMasterVolume);
            broadcastVolume(computeEffectiveVolume());
        }
    }

    /**
     * Computes the effective volume for this type, taking into account
     * the master volume if this is not {@link #MASTERVOLUME}.
     *
     * @return the effective volume (0.0 to 1.0)
     */
    private double computeEffectiveVolume() {
        return (this == MASTERVOLUME) ? volume : volume * masterVolume;
    }

    /**
     * Updates all registered audio managers with the given effective volume.
     *
     * @param effectiveVolume the volume to apply to all active audio resources
     */
    private void broadcastVolume(double effectiveVolume) {
        managers.stream()
                .filter(Objects::nonNull)
                .forEach(manager -> manager.getActiveAudio()
                        .forEach(aud -> aud.updateVolume(effectiveVolume)));
    }

    /**
     * Clamps a volume value to the valid range [0.0, 1.0].
     *
     * @param vol the volume to clamp
     * @return the clamped volume
     */
    private double clamp(double vol) {
        return Math.max(0, Math.min(vol, 1.0));
    }

    /**
     * Gets the current volume for this type.
     *
     * @return the current volume (0.0 to 1.0)
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Registers an {@link AudioManager} to this volume type.
     * <p>
     * Duplicate managers are ignored. Managers will receive volume updates
     * when this type's volume changes.
     *
     * @param manager the audio manager to register
     */
    public void addManager(AudioManager<? extends AudioResource> manager) {
        if (manager != null && !managers.contains(manager)) {
            managers.add(manager);
        }
    }

    /**
     * Removes a previously registered {@link AudioManager} from this type.
     *
     * @param manager the audio manager to remove
     */
    public void removeManager(AudioManager<? extends AudioResource> manager) {
        if (manager != null) {
            managers.remove(manager);
        }
    }

    /**
     * Returns an unmodifiable view of all registered audio managers for this type.
     *
     * @return a list of registered audio managers
     */
    public List<AudioManager<? extends AudioResource>> getManagers() {
        return Collections.unmodifiableList(managers);
    }
}