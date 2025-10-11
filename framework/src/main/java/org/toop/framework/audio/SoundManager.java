//package org.toop.framework.audio;
//
//import java.io.*;
//import java.util.*;
//import javafx.scene.media.MediaPlayer;
//import javax.sound.sampled.*;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.toop.framework.SnowflakeGenerator;
//import org.toop.framework.audio.events.AudioEvents;
//import org.toop.framework.eventbus.EventFlow;
//import org.toop.framework.resource.ResourceManager;
//import org.toop.framework.resource.ResourceMeta;
//import org.toop.framework.resource.resources.MusicAsset;
//import org.toop.framework.resource.resources.SoundEffectAsset;
//
//public class SoundManager {
//    private static final Logger logger = LogManager.getLogger(SoundManager.class);
//    private final Map<Long, Clip> activeSoundEffects = new HashMap<>();
//    private final HashMap<String, SoundEffectAsset> audioResources = new HashMap<>();
////    private final AudioVolumeManager audioVolumeManager = new AudioVolumeManager(this);
//
//    public SoundManager() {
//        // Get all Audio Resources and add them to a list.
//        for (ResourceMeta<SoundEffectAsset> asset :
//                ResourceManager.getAllOfType(SoundEffectAsset.class)) {
//            try {
//                this.addAudioResource(asset);
//            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        new EventFlow()
//                .listen(this::handlePlaySound)
//                .listen(this::handleStopSound)
//                .listen(
//                        AudioEvents.ClickButton.class,
//                        _ -> {
//                            try {
//                                playSound("medium-button-click.wav", false);
//                            } catch (UnsupportedAudioFileException
//                                    | LineUnavailableException
//                                    | IOException e) {
//                                logger.error(e);
//                            }
//                        });
//    }
//
//    private void handlePlaySound(AudioEvents.PlayEffect event) {
//        try {
//            this.playSound(event.fileName(), event.loop());
//        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void handleStopSound(AudioEvents.StopEffect event) {
//        this.stopSound(event.clipId());
//    }
//
//    private void addAudioResource(ResourceMeta<SoundEffectAsset> audioAsset)
//            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
//
//        this.audioResources.put(audioAsset.getName(), audioAsset.getResource());
//    }
//
//    private long playSound(String audioFileName, boolean loop)
//            throws UnsupportedAudioFileException, LineUnavailableException, IOException {
//        SoundEffectAsset asset = audioResources.get(audioFileName);
//
//        // Return -1 which indicates resource wasn't available
//        if (asset == null) {
//            logger.warn("Unable to load audio asset: {}", audioFileName);
//            return -1;
//        }
//
//        // Get a new clip from resource
//        Clip clip = asset.getNewClip();
//
//        // Set volume of clip
////        audioVolumeManager.updateSoundEffectVolume(clip);
//
//        // If supposed to loop make it loop, else just start it once
//        if (loop) {
//            clip.loop(Clip.LOOP_CONTINUOUSLY);
//        } else {
//            clip.start();
//        }
//
//        logger.debug("Playing sound: {}", asset.getFile().getName());
//
//        // Generate id for clip
//        long clipId = new SnowflakeGenerator().nextId();
//
//        // store it so we can stop it later
//        activeSoundEffects.put(clipId, clip);
//
//        // remove when finished (only for non-looping sounds)
//        clip.addLineListener(
//                event -> {
//                    if (event.getType() == LineEvent.Type.STOP && !clip.isRunning()) {
//                        activeSoundEffects.remove(clipId);
//                        clip.close();
//                    }
//                });
//
//        // Return id so it can be stopped
//        return clipId;
//    }
//
//    public void stopSound(long clipId) {
//        Clip clip = activeSoundEffects.get(clipId);
//
//        if (clip == null) {
//            return;
//        }
//
//        clip.stop();
//        clip.close();
//        activeSoundEffects.remove(clipId);
//    }
//
//    public void stopAllSounds() {
//        for (Clip clip : activeSoundEffects.values()) {
//            clip.stop();
//            clip.close();
//        }
//        activeSoundEffects.clear();
//    }
//
//    public Map<Long, Clip> getActiveSoundEffects() {
//        return this.activeSoundEffects;
//    }
//
//}
