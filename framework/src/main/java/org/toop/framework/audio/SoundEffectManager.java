package org.toop.framework.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.ResourceMeta;
import org.toop.framework.resource.resources.BaseResource;
import org.toop.framework.resource.resources.MusicAsset;
import org.toop.framework.resource.resources.SoundEffectAsset;
import org.toop.framework.resource.types.AudioResource;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SoundEffectManager<T extends AudioResource> implements org.toop.framework.audio.interfaces.SoundEffectManager<T> {
    private static final Logger logger = LogManager.getLogger(SoundEffectManager.class);
    private final HashMap<String, T> soundEffectResources;

    public <K extends BaseResource & AudioResource> SoundEffectManager(List<ResourceMeta<K>> resources) {
        // If there are duplicates, takes discards the first
        this.soundEffectResources = (HashMap<String, T>) resources
                .stream()
                .collect(Collectors.
                        toMap(ResourceMeta::getName, ResourceMeta::getResource, (a, b) -> b, HashMap::new));

    }

    @Override
    public Collection<T> getActiveAudio() {
        return this.soundEffectResources.values();
    }

    @Override
    public void play(String name, boolean loop) {
        T asset = soundEffectResources.get(name);

        if (asset == null) {
            logger.warn("Unable to load audio asset: {}", name);
            return;
        }

        asset.play();

        logger.debug("Playing sound: {}", asset.getName());
    }

    @Override
    public void stop(String name){
        T asset = soundEffectResources.get(name);

        if (asset == null) {
            logger.warn("Unable to load audio asset: {}", name);
            return;
        }

        asset.stop();

        logger.debug("Stopped sound: {}", asset.getName());
    }
}
