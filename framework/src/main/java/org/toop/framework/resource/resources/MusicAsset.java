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
        mediaPlayer.setOnStopped(null);
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
    public String getName() { return super.getFile().getName(); }

    @Override
    public void setOnEnd(Runnable run) {
        mediaPlayer.setOnEndOfMedia(run);
    }

    @Override
    public void setOnError(Runnable run) {
        mediaPlayer.setOnError(run);
    }

    @Override
    public void play() {
        getMediaPlayer().play();
    }

    @Override
    public void stop() {
        getMediaPlayer().stop();
    }

    @Override
    public long duration() {
        if (mediaPlayer != null) {
            return (long) this.mediaPlayer.getTotalDuration().toSeconds(); // Why is this a double? TODO: Fix cast
        }
        return 0;
    }

    @Override
    public long currentPosition() {
        if (mediaPlayer != null) {
            return (long) this.mediaPlayer.getCurrentTime().toSeconds(); // Same here. TODO: Fix cast
        }
        return 0;
    }

}
