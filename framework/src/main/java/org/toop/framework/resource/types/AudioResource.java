package org.toop.framework.resource.types;

public interface AudioResource {
    String getName();
    void updateVolume(double volume);
//    boolean isPlaying();
    void play();
    void stop();
    void setOnEnd(Runnable run);
    void setOnError(Runnable run);
}
