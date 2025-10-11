package org.toop.framework.audio.interfaces;

public interface SoundEffectManager<T> extends AudioManager<T> {
    void play(String name, boolean loop);
    void stop(long clipId);
}
