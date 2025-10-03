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
        AudioInputStream inputStream = this.getAudioStream();
        AudioFormat baseFormat = inputStream.getFormat();
        if (baseFormat.getSampleSizeInBits() > 16) inputStream = downSampleAudio(inputStream, baseFormat);
        clip.open(inputStream); // ^ Clip can only run 16 bit and lower, thus downsampling necessary.
        return clip;
    }

    // Generates a new audio stream from byte array
    private AudioInputStream getAudioStream() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(this.file);
    }

    private AudioInputStream downSampleAudio(AudioInputStream audioInputStream, AudioFormat baseFormat) {
        AudioFormat decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,       // force 16-bit
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false                   // little-endian
        );

        return AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
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
