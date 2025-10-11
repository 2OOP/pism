package org.toop.framework.audio;

import org.toop.framework.resource.resources.SoundEffectAsset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectManager implements org.toop.framework.audio.interfaces.SoundEffectManager<SoundEffectAsset> {
    private final Map<Long, SoundEffectAsset> activeSoundEffects = new HashMap<>();
    private final HashMap<String, SoundEffectAsset> audioResources = new HashMap<>();

    @Override
    public Collection<SoundEffectAsset> getActiveAudio() {
        return this.audioResources.values();
    }

    @Override
    public void play(String name, boolean loop) {

    }

    @Override
    public void stop(long clipId) {

    }
}
