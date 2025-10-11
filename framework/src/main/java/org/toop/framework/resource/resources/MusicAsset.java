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

    public MusicAsset(final File audioFile) {
        super(audioFile);
    }

    public MediaPlayer getMediaPlayer() {
        load();
        return mediaPlayer;
    }

    @Override
    public void load() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer(new Media(file.toURI().toString()));
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
    }
}
