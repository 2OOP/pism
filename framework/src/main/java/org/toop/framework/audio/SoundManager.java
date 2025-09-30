package org.toop.framework.audio;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.asset.Asset;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.asset.resources.AudioAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class SoundManager {
    private final AssetManager asm = AssetManager.getInstance();
    private final Map<Long, Clip> activeClips = new HashMap<>();
    private final HashMap<String, AudioAsset> audioResources = new HashMap<>();
    private final SnowflakeGenerator idGenerator = new SnowflakeGenerator(); // TODO: Don't create a new generator

    public SoundManager() {
        // Get all Audio Resources and add them to a list.
        for (Asset<AudioAsset> asset : asm.getAllOfType(AudioAsset.class)) {
            try {
                this.addAudioResource(asset);
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
        }
        new EventFlow()
                .listen(this::handlePlaySound)
                .listen(this::handleStopSound);
    }

    private void handlePlaySound(AudioEvents.PlayAudio event) {
        try {
            this.playSound(event.fileNameNoExtensionAndNoDirectory(), event.loop());
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStopSound(AudioEvents.StopAudio event) {
        this.stopSound(event.clipId());
    }

    private void addAudioResource(Asset<AudioAsset> audioAsset)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        this.audioResources.put(audioAsset.getName(), audioAsset.getResource());
    }

    private long playSound(String audioFileName, boolean loop) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        AudioAsset asset = audioResources.get(audioFileName);

        // Return -1 which indicates resource wasn't available
        if (asset == null){
            return -1;
        }

        // Get a new clip from resource
        Clip clip = asset.getNewClip();

        // If supposed to loop make it loop, else just start it once
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            clip.start();
        }

        // Generate id for clip
        long clipId = idGenerator.nextId();

        // store it so we can stop it later
        activeClips.put(clipId, clip); // TODO: Do on snowflake for specific sound to stop

        // remove when finished (only for non-looping sounds)
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && !clip.isRunning()) {
                activeClips.remove(clipId);
                clip.close();
            }
        });

        // Return id so it can be stopped
        return clipId;
    }

    public void stopSound(long clipId) {
        Clip clip = activeClips.get(clipId);

        if (clip == null) {
            return;
        }

        clip.stop();
        clip.close();
        activeClips.remove(clipId);
    }

    public void stopAllSounds() {
        for (Clip clip : activeClips.values()) {
            clip.stop();
            clip.close();
        }
        activeClips.clear();
    }
}
