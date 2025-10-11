package org.toop.framework.audio;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.BaseResource;
import org.toop.framework.resource.types.AudioResource;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class MusicManager<T extends AudioResource> implements org.toop.framework.audio.interfaces.MusicManager<T> {
    private static final Logger logger = LogManager.getLogger(MusicManager.class);
    private final Class<T> type;
    private final List<T> backgroundMusic = new LinkedList<>();
    private int playingIndex = 0;
    private ScheduledExecutorService scheduler;

    public MusicManager(Class<T> type) {
        this.type = type;

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownScheduler));
    }

    private void increasePlayingIndex() {
        playingIndex = (playingIndex + 1) % backgroundMusic.size();
    }

    @Override
    public Collection<T> getActiveAudio() {
        return backgroundMusic;
    }

    private void addBackgroundMusic(T musicAsset) {
        backgroundMusic.add(musicAsset);
    }

    private void shutdownScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
            logger.debug("MusicManager scheduler shut down.");
        }
    }

    @Override
    public void stop() {
        shutdownScheduler();
        Platform.runLater(() -> backgroundMusic.forEach(T::stop));
    }

    public void play() {
        backgroundMusic.clear();
        @SuppressWarnings("unchecked")
        List<T> resources = new ArrayList<>(ResourceManager.getAllOfType((Class<? extends BaseResource>) type)
                .stream()
                .map(e -> (T) e.getResource())
                .toList());
        Collections.shuffle(resources);
        backgroundMusic.addAll(resources);

        if (backgroundMusic.isEmpty()) return;

        shutdownScheduler();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        AtomicReference<T> current = new AtomicReference<>(backgroundMusic.get(playingIndex));

        Platform.runLater(() -> {
            T first = current.get();
            if (!first.isPlaying()) first.play();
        });

        scheduler.scheduleAtFixedRate(() -> {
            T track = current.get();
            if (!track.isPlaying()) {
                increasePlayingIndex();
                T next = backgroundMusic.get(playingIndex);
                current.set(next);
                Platform.runLater(() -> {
                    if (!next.isPlaying()) next.play();
                });
            }
        }, 500, 500, TimeUnit.MILLISECONDS);
    }
}
