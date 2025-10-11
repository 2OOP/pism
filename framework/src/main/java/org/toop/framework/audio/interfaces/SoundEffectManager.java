package org.toop.framework.audio.interfaces;

import org.toop.framework.resource.resources.SoundEffectAsset;
import org.toop.framework.resource.types.AudioResource;

public interface SoundEffectManager<T extends AudioResource> extends AudioManager<T> {
    void play(String name, boolean loop);
    void stop(long clipId);
}
