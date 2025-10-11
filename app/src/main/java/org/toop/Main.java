package org.toop;

import javafx.scene.media.MediaPlayer;
import org.toop.app.App;
import org.toop.framework.audio.AudioEventListener;
import org.toop.framework.audio.AudioVolumeManager;
import org.toop.framework.audio.MusicManager;
import org.toop.framework.audio.SoundEffectManager;
import org.toop.framework.audio.interfaces.AudioManager;
import org.toop.framework.audio.interfaces.VolumeManager;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;
import org.toop.framework.resource.ResourceLoader;
import org.toop.framework.resource.ResourceManager;

import javax.sound.sampled.Clip;

public final class Main {
    static void main(String[] args) {
        initSystems();
        App.run(args);
    }

    private static void initSystems() throws NetworkingInitializationException {
        ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/assets"));
        new Thread(NetworkingClientManager::new).start();
        new Thread(() -> {
            AudioEventListener a =
                    new AudioEventListener(
                        new MusicManager(),
                        new SoundEffectManager(),
                        new AudioVolumeManager()
                    ); a.initListeners();
        }).start();
    }
}
