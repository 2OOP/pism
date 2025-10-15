package org.toop.framework.audio.interfaces;

import org.toop.framework.resource.types.AudioResource;

public interface MusicManager<T extends AudioResource> extends AudioManager<T> {
    void play();
    void stop();
}
