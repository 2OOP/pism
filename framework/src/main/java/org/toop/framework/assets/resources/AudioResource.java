package org.toop.framework.assets.resources;

import javax.sound.sampled.*;
import java.io.*;

public class AudioResource extends Resource {
    public AudioResource(File audioFile) {
        super(audioFile);
    }

    public Clip getNewClip() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        Clip clip = AudioSystem.getClip();
        clip.open(this.getAudioStream());
        return clip;
    }

    private AudioInputStream getAudioStream() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(this.getStream());
    }
}
