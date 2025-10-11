package org.toop.framework.resource.resources;

import java.io.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.toop.framework.resource.types.AudioResource;
import org.toop.framework.resource.types.FileExtension;
import org.toop.framework.resource.types.LoadableResource;

@FileExtension({"mp3"})
public class MusicAsset extends BaseResource implements LoadableResource, AudioResource {
    private MediaPlayer mediaPlayer;
    private double volume;
    private boolean isPlaying = false;

    public MusicAsset(final File audioFile) {
        super(audioFile);
    }

    public MediaPlayer getMediaPlayer() {
        load();
        return mediaPlayer;
    }

    private void initPlayer() {
        mediaPlayer.setOnEndOfMedia(this::stop);
        mediaPlayer.setOnError(this::stop);
        mediaPlayer.setOnStopped(() -> isPlaying = false);
    }

    @Override
    public void load() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
            initPlayer();
            mediaPlayer.setVolume(volume);
        }
        this.isLoaded = true;
    }

    @Override
    public void unload() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void updateVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
        this.volume = volume;
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void play() {
        getMediaPlayer().play();
        isPlaying = true;
    }

    @Override
    public void stop() {
        getMediaPlayer().stop();
        isPlaying = false;
    }
}
