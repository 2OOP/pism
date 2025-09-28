package org.toop;

import org.toop.app.gui.LocalServerSelector;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;
import org.toop.framework.audio.AudioFilesManager;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Main {
    static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioFilesManager audioFiles = new AudioFilesManager("app/src/main/resources/audio/");
        String aFile = audioFiles.getAudioFile("hdchirp_88k_log.wav");
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(aFile));
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        clip.start();
        initSystems();
        javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new);
    }

    private static void initSystems() throws NetworkingInitializationException {
        new NetworkingClientManager();
    }
}