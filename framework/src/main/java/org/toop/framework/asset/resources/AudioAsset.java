package org.toop.framework.asset.resources;

import javax.sound.sampled.*;
import java.io.*;

@FileExtension({"wav"})
public class AudioAsset extends BaseResource implements LoadableResource {

    private AudioInputStream audioInputStream = null;
    private Clip clip = null;
    private boolean isLoaded = false;


    public AudioAsset(final File audioFile) {
        super(audioFile);
    }

    public AudioInputStream getAudioStream() {
        return this.audioInputStream;
    }

    @Override
    public void load() {
        try {
            this.audioInputStream = AudioSystem.getAudioInputStream(this.stream);
            Clip clip = AudioSystem.getClip();
            clip.open(this.audioInputStream);
            this.clip = clip;
            this.isLoaded = true;
        } catch (UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
        } catch (IOException e) { // TODO: Error handling
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unload() {
        this.clip.stop();
        this.clip.flush();
        this.clip.close();
        this.clip = null;
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    public Clip getClip() {
        if (!this.isLoaded) this.load();
        return this.clip;
    }

}
