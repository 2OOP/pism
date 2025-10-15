package org.toop.framework.resource.resources;

import java.io.*;
import javax.sound.sampled.*;

import org.toop.framework.resource.types.AudioResource;
import org.toop.framework.resource.types.FileExtension;
import org.toop.framework.resource.types.LoadableResource;

@FileExtension({"wav"})
public class SoundEffectAsset extends BaseResource implements LoadableResource, AudioResource {
    private final Clip clip = AudioSystem.getClip();

    private LineListener onEnd = null;
    private LineListener onError = null;

    private double volume = 100; // TODO: Find a better way to set volume on clip load

    public SoundEffectAsset(final File audioFile) throws LineUnavailableException {
        super(audioFile);
    }

    // Gets a new clip to play
    public Clip getClip() {
        if (!this.isLoaded()) {this.load();} return this.clip;
    }

    private AudioInputStream downSampleAudio(AudioInputStream audioInputStream, AudioFormat baseFormat) {
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
            if (this.isLoaded){
                return; // Return if it is already loaded
            }

            // Insert a new audio stream into the clip
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(this.getFile())));
            AudioFormat baseFormat = inputStream.getFormat();
            if (baseFormat.getSampleSizeInBits() > 16)
                inputStream = downSampleAudio(inputStream, baseFormat);
            this.clip.open(inputStream); // ^ Clip can only run 16 bit and lower, thus downsampling necessary.
            this.updateVolume(this.volume);
            this.isLoaded = true;
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unload() {
        if (!this.isLoaded) return; // Return if already unloaded

        if (clip.isRunning()) clip.stop(); // Stops playback of the clip

        clip.close(); // Releases native resources (empties buffer)

        this.getClip().removeLineListener(this.onEnd);
        this.getClip().removeLineListener(this.onError);

        this.onEnd = null;
        this.onError = null;

        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public void updateVolume(double volume) {
        {
            this.volume = volume;
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
    public String getName() {
        return this.getFile().getName();
    }

    @Override
    public void setOnEnd(Runnable run) {
        this.onEnd = event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                run.run();
            }
        };

        this.getClip().addLineListener(this.onEnd);
    }

    @Override
    public void setOnError(Runnable run) {
//        this.onError = event -> {
//            if (event.getType() == LineEvent.Type.STOP) {
//                run.run();
//            }
//        }; TODO
//
//        this.getClip().addLineListener(this.onEnd);

    }

    @Override
    public void play() {
        if (!isLoaded()) load();

        this.clip.setFramePosition(0); // rewind to the start
        this.clip.start();
    }

    @Override
    public void stop() {
        if (this.clip.isRunning()) this.clip.stop();
    }

    @Override
    public long duration() {
        return 0; // TODO
    }

    @Override
    public long currentPosition() {
        return 0; // TODO
    }


}
