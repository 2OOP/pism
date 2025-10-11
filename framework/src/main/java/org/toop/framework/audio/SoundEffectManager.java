package org.toop.framework.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.ResourceMeta;
import org.toop.framework.resource.resources.BaseResource;
import org.toop.framework.resource.resources.SoundEffectAsset;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SoundEffectManager implements org.toop.framework.audio.interfaces.SoundEffectManager<SoundEffectAsset> {
    private static final Logger logger = LogManager.getLogger(SoundEffectManager.class);
    private final HashMap<String, SoundEffectAsset> soundEffectResources;

    public SoundEffectManager(){
        // If there are duplicates, takes discards the first
        soundEffectResources = ResourceManager.getAllOfType(SoundEffectAsset.class).stream()
                .collect(Collectors.toMap(ResourceMeta::getName, ResourceMeta::getResource, (a, b) -> b, HashMap::new));

    }

    @Override
    public Collection<SoundEffectAsset> getActiveAudio() {
        return this.soundEffectResources.values();
    }

    @Override
    public void play(String name, boolean loop) {
        SoundEffectAsset asset = soundEffectResources.get(name);

        if (asset == null) {
            logger.warn("Unable to load audio asset: {}", name);
            return;
        }

        asset.play();
        // TODO: Volume of Sound Effect isn't set when loading. When loading an effect it will be full volume.
        logger.debug("Playing sound: {}", asset.getFile().getName());
    }

    @Override
    public void stop(String name){
        SoundEffectAsset asset = soundEffectResources.get(name);

        if (asset == null) {
            logger.warn("Unable to load audio asset: {}", name);
            return;
        }

        asset.stop();

        logger.debug("Stopped sound: {}", asset.getFile().getName());
    }
}
