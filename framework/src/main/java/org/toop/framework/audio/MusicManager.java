package org.toop.framework.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.dispatch.interfaces.Dispatcher;
import org.toop.framework.dispatch.JavaFXDispatcher;
import org.toop.annotations.TestsOnly;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.resource.types.AudioResource;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MusicManager<T extends AudioResource> implements org.toop.framework.audio.interfaces.MusicManager<T> {
    private static final Logger logger = LogManager.getLogger(MusicManager.class);

    private final List<T> backgroundMusic = new ArrayList<>();
    private final Dispatcher dispatcher;
    private final List<T> resources;
    private int playingIndex = 0;
    private boolean playing = false;
    private ScheduledExecutorService scheduler;


    public MusicManager(List<T> resources) {
        this.dispatcher = new JavaFXDispatcher();
        this.resources = resources;
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

    public void skip() {
        if (backgroundMusic.isEmpty()) return;
        stop();
        scheduler.shutdownNow();
        playingIndex = playingIndex + 1;
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

        scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable currentMusicTask = new Runnable() {
            @Override
            public void run() {
                GlobalEventBus.post(new AudioEvents.PlayingMusic(track.getName(), track.currentPosition(), track.duration()));
                scheduler.schedule(this, 1, TimeUnit.SECONDS);
            }
        };

        track.setOnEnd(() -> {
            scheduler.shutdownNow();
            playingIndex++;
            playCurrentTrack();
        });

        track.setOnError(() -> {
            scheduler.shutdownNow();
            logger.error("Error playing track: {}", track);
            backgroundMusic.remove(track);

            if (!backgroundMusic.isEmpty()) {
                playCurrentTrack();
            } else {
                playing = false;
            }
        });

        scheduler.schedule(currentMusicTask, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        if (!playing) return;

        playing = false;
        dispatcher.run(() -> backgroundMusic.forEach(T::stop));
    }
}
