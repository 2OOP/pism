package org.toop.framework.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.MusicAsset;

import java.util.*;

public class MusicManager implements org.toop.framework.audio.interfaces.MusicManager<MusicAsset> {
    private static final Logger logger = LogManager.getLogger(MusicManager.class);
    private final List<MusicAsset> backgroundMusic = new LinkedList<>();
    private int playingIndex = 0;
    private boolean playing = false;

    public MusicManager() {}

    @Override
    public Collection<MusicAsset> getActiveAudio() {
        return backgroundMusic;
    }

    private void addBackgroundMusic(MusicAsset musicAsset) {
        backgroundMusic.add(musicAsset);
    }

    public void play() {
        if (playing) {
            logger.warn("MusicManager is already playing.");
            return;
        }
        backgroundMusic.clear();
        List<MusicAsset> shuffledArray =
                new ArrayList<>(
                        ResourceManager.getAllOfType(MusicAsset.class).stream()
                                .map(ma ->
                                        initMediaPlayer(ma.getResource()))
                                .toList());
        Collections.shuffle(shuffledArray);
        backgroundMusic.addAll(shuffledArray);
        backgroundMusicPlayer();
    }

    private void backgroundMusicPlayer() {

        if (playingIndex >= backgroundMusic.size()) {
            playingIndex = 0;
        }

        MusicAsset ma = backgroundMusic.get(playingIndex);

        if (ma == null) {
            logger.error("Background music player is null. Queue: {}",
                    backgroundMusic.stream().map(e -> e.getMediaPlayer().getMedia().getSource()));
            return;
        }

        logger.info("Background music player is playing: {}", ma.getMediaPlayer().getMedia().getSource()); //TODO shorten to name
        ma.getMediaPlayer().play();
        this.playing = true;
    }

    private MusicAsset initMediaPlayer(MusicAsset ma) {
        ma.getMediaPlayer().setOnEndOfMedia(() -> ma.getMediaPlayer().stop());

        ma.getMediaPlayer().setOnError( () -> {
            logger.error("Error playing music: {}", ma.getMediaPlayer().getError()); // TODO
            backgroundMusic.remove(ma);
            ma.getMediaPlayer().stop();
        });

        ma.getMediaPlayer().setOnStopped( () -> {
            ma.getMediaPlayer().stop();
            playingIndex++;
            this.playing = false;
            backgroundMusicPlayer();
        });

        return ma;
    }
}
