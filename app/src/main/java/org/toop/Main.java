package org.toop;

import org.toop.app.gui.LocalServerSelector;
import org.toop.framework.asset.AssetLoader;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.audio.SoundManager;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;

public class Main {
    static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {

        AssetManager.initializeLoader(new File("app/src/main/resources/assets"));
        var b = new NetworkingClientManager();
        var c = new SoundManager();

        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("mainmenu.wav", true)).asyncPostEvent();
        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("sadtrombone.wav", true)).asyncPostEvent();
        Thread.sleep(200);
        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("mainmenu.wav", true)).asyncPostEvent();
        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("sadtrombone.wav", true)).asyncPostEvent();
        Thread.sleep(200);
        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("mainmenu.wav", true)).asyncPostEvent();
        new EventFlow().addPostEvent(new AudioEvents.PlayAudio("sadtrombone.wav", true)).asyncPostEvent();

        javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new);
    }

    private static void initSystems() throws NetworkingInitializationException, NotDirectoryException {
    }
}
