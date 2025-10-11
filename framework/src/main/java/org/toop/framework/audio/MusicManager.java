package org.toop.framework.audio;

import javafx.scene.media.MediaPlayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.MusicAsset;

import java.util.*;

public class MusicManager implements org.toop.framework.audio.interfaces.MusicManager<MediaPlayer> {
    private static final Logger logger = LogManager.getLogger(MusicManager.class);
//    private final List<MusicAsset> musicAssets = new ArrayList<>(); // TODO
    private final List<MediaPlayer> backgroundMusic = new LinkedList<>();
    private int playingIndex = 0;
    private boolean playing = false;

    public MusicManager() {}

    @Override
    public Collection<MediaPlayer> getActiveAudio() {
        return backgroundMusic;
    }

    private void addBackgroundMusic(MusicAsset musicAsset) {
        backgroundMusic.add(new MediaPlayer(musicAsset.getMedia()));
    }

    private void addBackgroundMusic(MediaPlayer mediaPlayer) {
        backgroundMusic.add(mediaPlayer);
    }

    public void play() { // TODO maybe remove VolumeManager from input
        backgroundMusic.clear();
        List<MediaPlayer> shuffledArray =
                new ArrayList<>(
                        ResourceManager.getAllOfType(MusicAsset.class).stream()
                                .map(ma ->
                                        initMediaPlayer(new MediaPlayer(ma.getResource().getMedia())))
                                .toList());
        Collections.shuffle(shuffledArray);
        backgroundMusic.addAll(shuffledArray);
        backgroundMusicPlayer();
    }

    private void backgroundMusicPlayer() {

        if (playingIndex >= backgroundMusic.size()) {
            playingIndex = 0;
        }

        MediaPlayer ma = backgroundMusic.get(playingIndex);

        if (ma == null) {
            logger.error("Background music player is null. Queue: {}",
                    backgroundMusic.stream().map(e -> e.getMedia().getSource()));
            return;
        }

        logger.info("Background music player is playing: {}", ma.getMedia().getSource()); //TODO shorten to name
        ma.play();
        this.playing = true;
    }

    private MediaPlayer initMediaPlayer(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);

        mediaPlayer.setOnError( () -> {
            logger.error("Error playing music: {}", mediaPlayer.getMedia().getSource());
            backgroundMusic.remove(mediaPlayer);
            mediaPlayer.stop();
        });

        mediaPlayer.setOnStopped( () -> {
            mediaPlayer.stop();
            playingIndex++;
            this.playing = false;
            backgroundMusicPlayer();
        });

        return mediaPlayer;
    }
}
