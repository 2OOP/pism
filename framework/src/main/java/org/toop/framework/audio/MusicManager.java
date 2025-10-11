package org.toop.framework.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.audio.interfaces.Dispatcher;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.BaseResource;
import org.toop.framework.resource.types.AudioResource;

import java.util.*;

public class MusicManager<T extends AudioResource> implements org.toop.framework.audio.interfaces.MusicManager<T> {
    private static final Logger logger = LogManager.getLogger(MusicManager.class);

    private final List<T> backgroundMusic = new LinkedList<>();
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

    // Used in unit testing
    MusicManager(Class<T> type, Dispatcher dispatcher, ResourceManager resourceManager) {
        this.dispatcher = dispatcher;
        this.resources = new ArrayList<>(resourceManager.getAllOfType((Class<? extends BaseResource>) type)
            .stream()
            .map(e -> (T) e.getResource())
            .toList());
        createShuffled();
    }

    @Override
    public Collection<T> getActiveAudio() {
        return backgroundMusic;
    }

    private void addBackgroundMusic(T musicAsset) {
        backgroundMusic.add(musicAsset);
    }

    private void createShuffled() {
        backgroundMusic.clear();
        Collections.shuffle(resources);
        backgroundMusic.addAll(resources);
    }

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

            current.setOnEnd(() -> {
                playingIndex++;
                playCurrentTrack();
            });

            current.setOnError(() -> {
                logger.error("Error playing track: {}", current);
                backgroundMusic.remove(current);
                if (!backgroundMusic.isEmpty()) {
                    playCurrentTrack();
                } else {
                    playing = false;
                }
            });
        });
    }

    public void stop() {
        if (!playing) return;

        playing = false;
        dispatcher.run(() -> backgroundMusic.forEach(T::stop));
    }
}
