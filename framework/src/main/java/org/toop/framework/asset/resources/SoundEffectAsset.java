package org.toop.framework.asset.resources;

import org.toop.framework.asset.types.FileExtension;
import org.toop.framework.asset.types.LoadableResource;

import javax.sound.sampled.*;
import java.io.*;

@FileExtension({"wav"})
public class SoundEffectAsset extends BaseResource implements LoadableResource {

    public SoundEffectAsset(final File audioFile) {
        super(audioFile);
    }

    // Gets a new clip to play
    public Clip getNewClip() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        if(!this.isLoaded()){
            this.load();
        }

        // Get a new clip from audio system
        Clip clip = AudioSystem.getClip();

        // Insert a new audio stream into the clip
        clip.open(this.getAudioStream());
        return clip;
    }

    // Generates a new audio stream from byte array
    private AudioInputStream getAudioStream() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(this.file);
    }

    @Override
    public void load() {
        try {
            this.getAudioStream();
            this.isLoaded = true;
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unload() {
        this.isLoaded = false; // TODO?
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }
}
