package org.toop.framework.audio.interfaces;

import org.toop.framework.resource.resources.SoundEffectAsset;
import org.toop.framework.resource.types.AudioResource;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public interface SoundEffectManager<T extends AudioResource> extends AudioManager<T> {
    void play(String name, boolean loop);
    void stop(String name);
}
