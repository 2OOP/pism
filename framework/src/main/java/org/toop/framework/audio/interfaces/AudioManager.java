package org.toop.framework.audio.interfaces;

import org.toop.framework.audio.VolumeTypes;

import java.util.Collection;

public interface AudioManager<T> {
    Collection<T> getActiveAudio();
}
