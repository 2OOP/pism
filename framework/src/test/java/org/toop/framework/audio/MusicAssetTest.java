package org.toop.framework.audio;

import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.toop.framework.resource.resources.MusicAsset;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;


public class MusicAssetTest {

    private static boolean started = false;
    private MusicAsset musicAsset;

    @BeforeAll
    static void initJavaFX() throws Exception {
        if (started) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.startup(countDownLatch::countDown);
        countDownLatch.await();
        started = true;
    }

    private static void run(Runnable action) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private MusicAsset createMusicAsset() {
        URL asset = getClass().getClassLoader().getResource("test.mp3");
        assertNotNull(asset, "Test MP3 not found");
        File testFile = new File(asset.getFile());
        assertTrue(testFile.exists(), "File doesn't exist");
        return new MusicAsset(testFile);
    }

    private void wait(MediaPlayer player, MediaPlayer.Status status, long timeout) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (player.getStatus() != status && System.currentTimeMillis() - start < timeout) {
            Thread.sleep(10);
        }
    }

    @Test
    void loadMusicAssetAsLoaded() {
        MusicAsset musicAsset = createMusicAsset();
        run(musicAsset::load);
        assertTrue(musicAsset.isLoaded());
        assertNotNull(musicAsset.getMediaPlayer());
    }

    @Test
    void loadMusicAssetAsNotLoaded() {
        MusicAsset musicAsset = createMusicAsset();
        run(musicAsset::load);
        assertTrue(musicAsset.isLoaded());
        run(musicAsset::unload);
        assertFalse(musicAsset.isLoaded());
        assertNotNull(musicAsset.getMediaPlayer());
    }

    @Test
    void updateVolume() {
        MusicAsset musicAsset = createMusicAsset();
        musicAsset.updateVolume(0.5);
        assertEquals(0.5, musicAsset.getVolume(), 0.0000001);
    }

    @Test
    void playPauseStop() throws InterruptedException {
        MusicAsset musicAsset = createMusicAsset();
        run(musicAsset::load);

        run(musicAsset::play);
        wait(musicAsset.getMediaPlayer(), MediaPlayer.Status.PLAYING, 500);
        assertEquals(MediaPlayer.Status.PLAYING, musicAsset.getMediaPlayer().getStatus());

        run(musicAsset::pause);
        wait(musicAsset.getMediaPlayer(), MediaPlayer.Status.PAUSED, 500);
        assertEquals(MediaPlayer.Status.PAUSED, musicAsset.getMediaPlayer().getStatus());

        run(musicAsset::stop);
        wait(musicAsset.getMediaPlayer(), MediaPlayer.Status.STOPPED, 500);
        assertEquals(MediaPlayer.Status.STOPPED, musicAsset.getMediaPlayer().getStatus());
    }

    @Test
    void duration() throws InterruptedException {
        MusicAsset musicAsset = createMusicAsset();
        run(musicAsset::load);
        wait(musicAsset.getMediaPlayer(), MediaPlayer.Status.READY, 500);
        Duration duration = musicAsset.getMediaPlayer().getTotalDuration();
        assertTrue(duration.toMillis() > 200); // file is shorter than 1 second, so gotta put it to milliseconds
    }

    @Test
    void currentPosition() throws InterruptedException {
        MusicAsset musicAsset = createMusicAsset();
        run(musicAsset::load);

        run(musicAsset::play);
        wait(musicAsset.getMediaPlayer(), MediaPlayer.Status.PLAYING, 500);
        Thread.sleep(200);
        Duration currentPos = musicAsset.getMediaPlayer().getCurrentTime();
        assertTrue(currentPos.toMillis() > 0); // file is shorter than 1 second, so gotta put it to milliseconds
    }
}

