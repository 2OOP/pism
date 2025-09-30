package org.toop.framework.audio;

import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.assets.Asset;
import org.toop.framework.assets.AssetManager;
import org.toop.framework.assets.resources.AudioResource;
import org.toop.framework.assets.resources.Resource;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class SoundManager {
    private final Map<Long, Clip> activeClips = new HashMap<>();
    private final HashMap<String, AudioInputStream> audioStreams = new HashMap<>();
    private final SnowflakeGenerator idGenerator;

    public SoundManager(AssetManager asm) {
        this.idGenerator = new SnowflakeGenerator(); // TODO: don't create a new snowflake generator.
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
        try {
            this.playSound(event.fileNameNoExtensionAndNoDirectory(), event.loop());
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStopSound(AudioEvents.StopAudio event) {
        this.stopSound(event.fileNameNoExtensionAndNoDirectory());
    }

    private void addClip(Asset<AudioResource> audioAsset)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioResource audioResource = audioAsset.getResource();
        this.audioStreams.put(audioAsset.getName(), audioResource.getAudioStream());
    }

    private long playSound(String audioFileName, boolean loop) throws LineUnavailableException, IOException {
        // Get audioStream
        AudioInputStream audioStream = audioStreams.get(audioFileName);

        // Return -1 if audiStream doesn't exist
        if (audioStream == null) {
            return -1;
        }

        // Get a clip and open the audioStream
        final Clip clip = AudioSystem.getClip();
        clip.open(audioStream);


        // If loop make it loop, else just start it once
        if (loop){
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            clip.start();
        }

        // Generate ID
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

        return clipId;
    }

    public HashMap<String, AudioInputStream> getAudioStreams() {
        return this.audioStreams;
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
