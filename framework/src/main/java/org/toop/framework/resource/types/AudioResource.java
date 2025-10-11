package org.toop.framework.resource.types;

public interface AudioResource {
    void updateVolume(double volume);
    boolean isPlaying();
    void play();
    void stop();
}
