package org.toop.framework.assets.resources;

import javax.sound.sampled.*;
import java.io.*;

public class AudioResource extends Resource {

    // Constructor
    public AudioResource(File audioFile) {
        super(audioFile);
    }

    // Gets a new clip to play
    public Clip getNewClip() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        // Get a new clip from audio system
        Clip clip = AudioSystem.getClip();

        // Insert a new audio stream into the clip
        clip.open(this.getAudioStream());
        return clip;
    }

    // Generates a new audio stream from byte array
    private AudioInputStream getAudioStream() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(this.getStream());
    }
}
