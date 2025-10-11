package org.toop.framework.audio;

import org.toop.framework.resource.resources.SoundEffectAsset;

import javax.sound.sampled.Clip;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SoundEffectManager implements org.toop.framework.audio.interfaces.SoundEffectManager<Clip> {
    private final Map<Long, Clip> activeSoundEffects = new HashMap<>();
    private final HashMap<String, SoundEffectAsset> audioResources = new HashMap<>();


    public Collection<Clip> getActiveAudio() {
        return this.activeSoundEffects.values();
    }

    @Override
    public void play(String name, boolean loop) {

    }

    @Override
    public void stop(long clipId) {

    }
}
