package org.toop;

import org.toop.app.gui.LocalServerSelector;
import org.toop.framework.audio.AudioFiles;
import org.toop.framework.audio.SoundManager;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.file.NotDirectoryException;

public class Main {
    static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        initSystems();
        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("mainmenu", true)).asyncPostEvent();
        javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new);
    }

    private static void initSystems() throws NetworkingInitializationException, NotDirectoryException {
        new NetworkingClientManager();
        new SoundManager(new AudioFiles("app/src/main/resources/audio/"));
    }
}
