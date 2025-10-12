package org.toop.framework.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.dispatch.interfaces.Dispatcher;
import org.toop.framework.dispatch.JavaFXDispatcher;
import org.toop.framework.annotations.TestsOnly;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.BaseResource;
import org.toop.framework.resource.types.AudioResource;

import java.util.*;

public class MusicManager<T extends AudioResource> implements org.toop.framework.audio.interfaces.MusicManager<T> {
    private static final Logger logger = LogManager.getLogger(MusicManager.class);

    private final List<T> backgroundMusic = new ArrayList<>();
    private final Dispatcher dispatcher;
    private final List<T> resources;
    private int playingIndex = 0;
    private boolean playing = false;

    public MusicManager(Class<T> type) {
        this.dispatcher = new JavaFXDispatcher();
        this.resources = new ArrayList<>(ResourceManager.getAllOfType((Class<? extends BaseResource>) type)
                .stream()
                .map(e -> (T) e.getResource())
                .toList());
        createShuffled();
    }

    /**
     * {@code @TestsOnly} DO NOT USE
     */
    @TestsOnly
    public MusicManager(List<T> resources, Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.resources = new ArrayList<>(resources);
        backgroundMusic.addAll(resources);
    }

    @Override
    public Collection<T> getActiveAudio() {
        return backgroundMusic;
    }

    void addBackgroundMusic(T musicAsset) {
        backgroundMusic.add(musicAsset);
    }

    private void createShuffled() {
        backgroundMusic.clear();
        Collections.shuffle(resources);
        backgroundMusic.addAll(resources);
    }

    @Override
    public void play() {
        if (playing) {
            logger.warn("MusicManager is already playing.");
            return;
        }

        if (backgroundMusic.isEmpty()) return;

        playingIndex = 0;
        playing = true;
        playCurrentTrack();
    }

    // Used in testing
    void play(int index) {
        if (playing) {
            logger.warn("MusicManager is already playing.");
            return;
        }

        if (backgroundMusic.isEmpty()) return;

        playingIndex = index;
        playing = true;
        playCurrentTrack();
    }

    private void playCurrentTrack() {
        if (playingIndex >= backgroundMusic.size()) {
            playingIndex = 0;
        }

        T current = backgroundMusic.get(playingIndex);

        if (current == null) {
            logger.error("Current track is null!");
            return;
        }

        dispatcher.run(() -> {
            current.play();

            setTrackRunnable(current);

        });
    }

    private void setTrackRunnable(T track) {
        track.setOnEnd(() -> {
            playingIndex++;
            playCurrentTrack();
        });

        track.setOnError(() -> {
            logger.error("Error playing track: {}", track);
            backgroundMusic.remove(track);

            if (!backgroundMusic.isEmpty()) {
                playCurrentTrack();
            } else {
                playing = false;
            }
        });
    }

    @Override
    public void stop() {
        if (!playing) return;

        playing = false;
        dispatcher.run(() -> backgroundMusic.forEach(T::stop));
    }
}
