package org.toop.framework.audio;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.assets.Asset;
import org.toop.framework.assets.AssetManager;
import org.toop.framework.assets.resources.AudioResource;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class SoundManager {
    private final Map<String, Clip> activeClips = new HashMap<>();
    private final HashMap<String, Clip> clips = new HashMap<>();
    private final HashMap<String, AudioResource> audioResources = new HashMap<>();
    private final SnowflakeGenerator idGenerator = new SnowflakeGenerator(); // TODO: Don't create a new generator

    public SoundManager(AssetManager asm) {
        // Get all Audio Resources and add them to a list.
        for (Asset<AudioResource> resource : asm.getAllResourceOfType(AudioResource.class).values()) {
            try {
                addClip(resource);
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
        }
        new EventFlow()
                .listen(this::handlePlaySound)
                .listen(this::handleStopSound);
    }

    private void handlePlaySound(AudioEvents.PlayAudio event) {
        this.playSound(event.fileNameNoExtensionAndNoDirectory(), event.loop());
    }

    private void handleStopSound(AudioEvents.StopAudio event) {
        this.stopSound(event.fileNameNoExtensionAndNoDirectory());
    }

    private void addClip(Asset<AudioResource> audioAsset)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioResource audioResource = audioAsset.getResource();

        this.clips.put(audioAsset.getName(), audioResource.getNewClip());
        this.audioResources.put(audioAsset.getName(), audioResource);
    }

    private void playSound(String audioFileName, boolean loop) {
        try {
            AudioResource resource = audioResources.get(audioFileName);

            if (resource == null){
                return;
            }

            Clip clip = resource.getNewClip();

            // If loop make it loop, else just start it once
            if (loop){
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            else {
                clip.start();
            }

            // store it so we can stop it later
            activeClips.put(audioFileName, clip); // TODO: Do on snowflake for specific sound to stop

            // remove when finished (only for non-looping sounds)
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && !clip.isRunning()) {
                    activeClips.remove(audioFileName);
                    clip.close();
                }
            });
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Clip> getClips() {
        return this.clips;
    }

    public void stopSound(String audioFileName) {
        Clip clip = activeClips.get(audioFileName);

        if (clip == null) {
            return;
        }

        clip.stop();
        clip.close();
        activeClips.remove(audioFileName);
    }

    public void stopAllSounds() {
        for (Clip clip : activeClips.values()) {
            clip.stop();
            clip.close();
        }
        activeClips.clear();
    }
}
