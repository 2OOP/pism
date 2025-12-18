package org.toop.framework.audio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.framework.dispatch.interfaces.Dispatcher;
import org.toop.framework.eventbus.GlobalEventBus;
import org.toop.framework.resource.resources.BaseResource;
import org.toop.framework.resource.types.AudioResource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MockAudioResource extends BaseResource implements AudioResource {
    boolean played = false;
    boolean stopped = false;
    boolean paused = false;
    Runnable onEnd;
    Runnable onError;

    public MockAudioResource(String name) {
        super(new File(name));
    }

    public void triggerError() {
        if (onError != null) {
            onError.run();
        }
    }

    public void triggerEnd() {
        if (onEnd != null) {
            onEnd.run();
        }
    }

    @Override
    public String getName() {
        return super.getFile().getName();
    }

    @Override
    public void play() {
        played = true;
    }

    @Override
    public void stop() {
        stopped = true;
    }

    public void pause() { paused = true; }

    @Override
    public long duration() {
        return 0;
    }

    @Override
    public long currentPosition() {
        return 0;
    }

    @Override
    public void setOnEnd(Runnable callback) {
        onEnd = callback;
    }

    @Override
    public void setOnError(Runnable callback) {
        onError = callback;
    }

    @Override
    public void updateVolume(double volume) {}
}

public class MusicManagerTest {

    private Dispatcher dispatcher;
    private MockAudioResource track1;
    private MockAudioResource track2;
    private MockAudioResource track3;
    private MusicManager<MockAudioResource> manager;

    @BeforeEach
    void setUp() {
        dispatcher = Runnable::run;

        track1 = new MockAudioResource("track1");
        track2 = new MockAudioResource("track2");
        track3 = new MockAudioResource("track3");

        List<MockAudioResource> resources = List.of(track1, track2, track3);

        manager = new MusicManager<>(GlobalEventBus.get(), resources, dispatcher);
    }

    @Test
    void testPlaySingleTrack() {
        manager.play();
        assertTrue(track1.played || track2.played || track3.played,
                "At least one track should have played");
    }

    @Test
    void testPlayMultipleTimesDoesNotRestart() {
        manager.play();
        track1.played = false;
        manager.play();
        assertFalse(track1.played, "Second play call should not restart tracks");
    }

    @Test
    void testStopStopsAllTracks() {
        manager.play();
        manager.stop();
        assertTrue(track1.stopped && track2.stopped && track3.stopped,
                "All tracks should be stopped");
    }

    @Test
    void testAutoAdvanceTracks() {
        track1.played = false;
        track2.played = false;
        track3.played = false;

        manager.play();
        track1.triggerEnd();
        track2.triggerEnd();

        assertTrue(track1.played, "Track1 should play, played %s instead");
        assertTrue(track2.played, "Track2 should play after track1 ends");
        assertTrue(track3.played, "Track3 should play after track2 ends");
    }

    @Test
    void testTrackErrorRemovesTrackAndPlaysNext() {
        manager.play();
        track1.triggerError();

        assertFalse(manager.getActiveAudio().contains(track1),
                "Track1 should be removed after error");
        assertTrue(track2.played, "Track2 should play after track1 error");
    }

    @Test
    void testPlayWithEmptyPlaylistDoesNothing() {
        manager.getActiveAudio().clear();
        manager.play();
        assertFalse(track1.played || track2.played || track3.played,
                "No tracks should play if playlist is empty");
    }

    @Test
    void testMultiplePlayStopSequences() {
        manager.play();
        manager.stop();
        manager.play();
        assertTrue(track1.played || track2.played || track3.played,
                "Tracks should play again after stopping");
    }

    @Test
    void testPlayingIndexWrapsAround() {
        track1.played = false;
        track2.played = false;
        track3.played = false;

        manager.play();
        track1.triggerEnd();
        track2.triggerEnd();
        track3.triggerEnd();

        assertTrue(track1.played, "Track1 should play again after loop");
        assertTrue(track2.played, "Track2 should play");
        assertTrue(track3.played, "Track3 should play");
    }

    /**
     * Test for many tracks playing sequentially one after another
     */
    @Test
    void testSequentialMultipleTracks() {
        List<MockAudioResource> manyTracks = new ArrayList<>();
        for (int i = 1; i <= 1_000; i++) {
            manyTracks.add(new MockAudioResource("track" + i));
        }

        MusicManager<MockAudioResource> multiManager = new MusicManager<>(GlobalEventBus.get(), manyTracks, dispatcher);

        for (int i = 0; i < manyTracks.size() - 1; i++) {
            multiManager.play();
            manyTracks.get(i).triggerEnd();
        }

        for (int i = 0; i < manyTracks.size(); i++) {
            assertTrue(manyTracks.get(i).played, "Track " + (i + 1) + " should have played sequentially");
        }
    }
}

