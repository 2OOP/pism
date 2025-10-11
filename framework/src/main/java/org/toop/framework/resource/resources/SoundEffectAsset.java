package org.toop.framework.resource.resources;

import java.io.*;
import java.nio.file.Files;
import javax.sound.sampled.*;

import org.toop.framework.resource.types.AudioResource;
import org.toop.framework.resource.types.FileExtension;
import org.toop.framework.resource.types.LoadableResource;

@FileExtension({"wav"})
public class SoundEffectAsset extends BaseResource implements LoadableResource, AudioResource {
    private byte[] rawData;
    private Clip clip = null;

    public SoundEffectAsset(final File audioFile) {
        super(audioFile);
    }

    // Gets a new clip to play
    public Clip getNewClip()
            throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        // Get a new clip from audio system
        Clip clip = AudioSystem.getClip();

        // Insert a new audio stream into the clip
        AudioInputStream inputStream = this.getAudioStream();
        AudioFormat baseFormat = inputStream.getFormat();
        if (baseFormat.getSampleSizeInBits() > 16)
            inputStream = downSampleAudio(inputStream, baseFormat);
        clip.open(
                inputStream); // ^ Clip can only run 16 bit and lower, thus downsampling necessary.
        this.clip = clip;
        return clip;
    }

    // Generates a new audio stream from byte array
    private AudioInputStream getAudioStream() throws UnsupportedAudioFileException, IOException {
        // Check if raw data is loaded into memory
        if (!this.isLoaded()) {
            this.load();
        }

        // Turn rawData into an input stream and turn that into an audio input stream;
        return AudioSystem.getAudioInputStream(new ByteArrayInputStream(this.rawData));
    }

    private AudioInputStream downSampleAudio(
            AudioInputStream audioInputStream, AudioFormat baseFormat) {
        AudioFormat decodedFormat =
                new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16, // force 16-bit
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false // little-endian
                        );

        return AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);
    }

    @Override
    public void load() {
        try {
            this.rawData = Files.readAllBytes(file.toPath());
            this.isLoaded = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unload() {
        this.rawData = null;
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public void updateVolume(double volume) {
        {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl =
                        (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float dB =
                        (float)
                                (Math.log10(Math.max(volume, 0.0001))
                                        * 20.0); // convert linear to dB
                dB = Math.max(min, Math.min(max, dB));
                volumeControl.setValue(dB);
            }
        }
    }

    @Override
    public void setOnEnd(Runnable run) {
        // TODO
    }

    @Override
    public void setOnError(Runnable run) {
        // TODO
    }

    @Override
    public void play() {
        // TODO
    }

    @Override
    public void stop() {
        // TODO
    }

}
