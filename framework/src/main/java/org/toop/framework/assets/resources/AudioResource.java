package org.toop.framework.assets.resources;

import javax.sound.sampled.*;
import java.io.*;

public class AudioResource extends Resource implements ResourceType<AudioResource> {

    private AudioInputStream audioInputStream = null;
    private Clip clip = null;


    public AudioResource(File audioFile) {
        super(audioFile);
    }

    public AudioInputStream getAudioStream(){
        return this.audioInputStream;
    }

    public Clip getClip() {
        return this.clip;
    }

    public AudioResource load() {
        try {
            this.audioInputStream = AudioSystem.getAudioInputStream(this.stream);
            Clip clip = AudioSystem.getClip();
            clip.open(this.audioInputStream);
            this.clip = clip;
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (IOException e) { // TODO: Error handling
            throw new RuntimeException(e);
        }
        return this;
    }
}
