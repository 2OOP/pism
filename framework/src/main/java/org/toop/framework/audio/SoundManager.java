package org.toop.framework.audio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.ResourceMeta;
import org.toop.framework.asset.resources.MusicAsset;
import org.toop.framework.asset.resources.SoundEffectAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.util.*;
import javax.sound.sampled.*;

public class SoundManager {
    private static final Logger logger = LogManager.getLogger(SoundManager.class);
    private final List<MediaPlayer> activeMusic = new ArrayList<>();
    private final Queue<MusicAsset> backgroundMusicQueue = new LinkedList<>();
    private final Map<Long, Clip> activeSoundEffects = new HashMap<>();
    private final HashMap<String, SoundEffectAsset> audioResources = new HashMap<>();
    private final SnowflakeGenerator idGenerator = new SnowflakeGenerator(); // TODO: Don't create a new generator

    private double volume = 1.0;

    public SoundManager() {
        // Get all Audio Resources and add them to a list.
        for (ResourceMeta<SoundEffectAsset> asset : ResourceManager.getAllOfType(SoundEffectAsset.class)) {
            try {
                this.addAudioResource(asset);
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
                throw new RuntimeException(e);
            }
        }
        new EventFlow()
                .listen(this::handlePlaySound)
                .listen(this::handleStopSound)
                .listen(this::handleMusicStart)
                .listen(this::handleVolumeChange)
                .listen(this::handleGetCurrentVolume)
                .listen(AudioEvents.ClickButton.class, _ -> {
                    try {
                        playSound("medium-button-click.wav", false);
                    } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                        logger.error(e);
                    }
                });
    }

    private void handlePlaySound(AudioEvents.PlayEffect event) {
        try {
            this.playSound(event.fileName(), event.loop());
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleStopSound(AudioEvents.StopEffect event) {
        this.stopSound(event.clipId());
    }

    private void addAudioResource(ResourceMeta<SoundEffectAsset> audioAsset)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {

        this.audioResources.put(audioAsset.getName(), audioAsset.getResource());
    }

    private void handleVolumeChange(AudioEvents.ChangeVolume event) {
		double newVolume = event.newVolume() / 100.0;
        if (newVolume > 1.0) this.volume = 1.0;
        else this.volume = Math.max(newVolume, 0.0);
        for (MediaPlayer mediaPlayer : this.activeMusic) {
            mediaPlayer.setVolume(this.volume);
        }
    }

    private void handleGetCurrentVolume(AudioEvents.GetCurrentVolume event) {
        new EventFlow().addPostEvent(new AudioEvents.GetCurrentVolumeReponse(volume * 100.0, event.snowflakeId()))
                .asyncPostEvent();
    }

    private void handleMusicStart(AudioEvents.StartBackgroundMusic e) {
        backgroundMusicQueue.clear();
        List<MusicAsset> shuffledArray = new ArrayList<>(ResourceManager.getAllOfType(MusicAsset.class)
                .stream()
                .map(ResourceMeta::getResource)
                .toList());
        Collections.shuffle(shuffledArray);
        backgroundMusicQueue.addAll(
            shuffledArray
        );
        backgroundMusicPlayer();
    }

    private void addBackgroundMusic(MusicAsset musicAsset) {
        backgroundMusicQueue.add(musicAsset);
    }

    private void backgroundMusicPlayer() {
        MusicAsset ma = backgroundMusicQueue.poll();
        if (ma == null) return;

        MediaPlayer mediaPlayer = new MediaPlayer(ma.getMedia());

        mediaPlayer.setOnEndOfMedia(() -> {
            addBackgroundMusic(ma);
            activeMusic.remove(mediaPlayer);
            mediaPlayer.dispose();
            ma.unload();
            backgroundMusicPlayer(); // play next
        });

        mediaPlayer.setOnStopped(() -> {
            addBackgroundMusic(ma);
            activeMusic.remove(mediaPlayer);
            ma.unload();
        });

        mediaPlayer.setOnError(() -> {
            addBackgroundMusic(ma);
            activeMusic.remove(mediaPlayer);
            ma.unload();
        });

        mediaPlayer.setVolume(this.volume);
        mediaPlayer.play();
        activeMusic.add(mediaPlayer);
        logger.info("Playing background music: {}", ma.getFile().getName());
        logger.info("Background music next in line: {}", backgroundMusicQueue.peek().getFile().getName());
    }

    private long playSound(String audioFileName, boolean loop) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        SoundEffectAsset asset = audioResources.get(audioFileName);

        // Return -1 which indicates resource wasn't available
        if (asset == null) {
            logger.warn("Unable to load audio asset: {}", audioFileName);
            return -1;
        }

        // Get a new clip from resource
        Clip clip = asset.getNewClip();

        // If supposed to loop make it loop, else just start it once
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            clip.start();
        }

        logger.debug("Playing sound: {}", asset.getFile().getName());

        // Generate id for clip
        long clipId = idGenerator.nextId();

        // store it so we can stop it later
        activeSoundEffects.put(clipId, clip); // TODO: Do on snowflake for specific sound to stop

        // remove when finished (only for non-looping sounds)
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && !clip.isRunning()) {
                activeSoundEffects.remove(clipId);
                clip.close();
            }
        });

        // Return id so it can be stopped
        return clipId;
    }

    public void stopSound(long clipId) {
        Clip clip = activeSoundEffects.get(clipId);

        if (clip == null) {
            return;
        }

        clip.stop();
        clip.close();
        activeSoundEffects.remove(clipId);
    }

    public void stopAllSounds() {
        for (Clip clip : activeSoundEffects.values()) {
            clip.stop();
            clip.close();
        }
        activeSoundEffects.clear();
    }
}
