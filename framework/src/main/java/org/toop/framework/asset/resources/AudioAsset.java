package org.toop.framework.asset.resources;

import javax.sound.sampled.*;
import java.io.*;

@FileExtension({"wav"})
public class AudioAsset extends BaseResource implements LoadableResource {

    public AudioAsset(final File audioFile) {
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
        return AudioSystem.getAudioInputStream(this.getInputStream());
    }
}
