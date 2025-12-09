package org.toop.framework.audio.interfaces;

import org.toop.framework.audio.VolumeControl;


/**
 * Interface for managing audio volumes in the application.
 * <p>
 * Implementations of this interface are responsible for controlling the volume levels
 * of different categories of audio (e.g., master volume, music, sound effects) and
 * updating the associated audio managers or resources accordingly.
 * </p>
 *
 * <p>Typical responsibilities include:</p>
 * <ul>
 *   <li>Setting the volume for a specific category (master, music, FX).</li>
 *   <li>Retrieving the current volume of a category.</li>
 *   <li>Ensuring that changes in master volume propagate to dependent audio categories.</li>
 *   <li>Interfacing with {@link org.toop.framework.audio.interfaces.AudioManager} to update active audio resources.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * VolumeManager volumeManager = ...;
 * // Set master volume to 80%
 * volumeManager.setVolume(0.8, VolumeControl.MASTERVOLUME);
 *
 * // Set music volume to 50% of master
 * volumeManager.setVolume(0.5, VolumeControl.MUSIC);
 *
 * // Retrieve current FX volume
 * double fxVolume = volumeManager.getVolume(VolumeControl.FX);
 * }</pre>
 */
public interface VolumeManager {

    /**
     *
     * Sets the volume to for the specified {@link VolumeControl}.
     *
     * @param newVolume The volume to be set to.
     * @param type The type of volume to change.
     */
    void setVolume(double newVolume, VolumeControl type);

    /**
     * Gets the current volume for the specified {@link VolumeControl}.
     *
     * @param type the type of volume to get.
     * @return The volume as a {@link Double}
     */
    double getVolume(VolumeControl type);
}
