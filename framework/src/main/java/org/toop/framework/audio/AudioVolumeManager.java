package org.toop.framework.audio;

import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.audio.interfaces.VolumeManager;
import org.toop.framework.resource.types.AudioResource;

/**
 * Concrete implementation of {@link VolumeManager} that delegates volume control
 * to the {@link VolumeControl} enum.
 * <p>
 * This class acts as a central point for updating volume levels for different
 * audio categories (MASTER, FX, MUSIC) and for registering audio managers
 * to the appropriate volume types.
 * </p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *     <li>Set and get volume levels for each {@link VolumeControl} category.</li>
 *     <li>Register {@link AudioManager} instances to specific volume types so
 *         that their active audio resources receive volume updates automatically.</li>
 *     <li>Automatically scales non-master volumes according to the current master volume.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * AudioVolumeManager volumeManager = new AudioVolumeManager();
 *
 * // Register music manager to MUSIC volume type
 * volumeManager.registerManager(VolumeControl.MUSIC, musicManager);
 *
 * // Set master volume to 80%
 * volumeManager.setVolume(0.8, VolumeControl.MASTERVOLUME);
 *
 * // Set FX volume to 50% of master
 * volumeManager.setVolume(0.5, VolumeControl.FX);
 *
 * // Retrieve current MUSIC volume
 * double musicVol = volumeManager.getVolume(VolumeControl.MUSIC);
 * }</pre>
 */
public class AudioVolumeManager implements VolumeManager {

    /**
     * Sets the volume for a specific volume type.
     * <p>
     * This method automatically takes into account the master volume
     * for non-master types.
     *
     * @param newVolume the desired volume level (0.0 to 1.0)
     * @param type the {@link VolumeControl} category to update
     */
    @Override
    public void setVolume(double newVolume, VolumeControl type) {
        type.setVolume(newVolume, VolumeControl.MASTERVOLUME.getVolume());
    }

    /**
     * Returns the current volume for the specified {@link VolumeControl} category.
     *
     * @param type the volume category
     * @return the current volume (0.0 to 1.0)
     */
    @Override
    public double getVolume(VolumeControl type) {
        return type.getVolume();
    }

    /**
     * Registers an {@link AudioManager} with the specified {@link VolumeControl} category.
     * <p>
     * All active audio resources managed by the given {@link AudioManager} will
     * automatically receive volume updates when the volume type changes.
     *
     * @param type the volume type to register the manager under
     * @param manager the audio manager to register
     * @return the current {@link AudioVolumeManager} instance (for method chaining)
     */
    public AudioVolumeManager registerManager(VolumeControl type, AudioManager<? extends AudioResource> manager) {
        if (manager != null) {
            type.addManager(manager);
        }
        return this;
    }
}
