package org.toop;

import org.toop.app.App;
import org.toop.framework.audio.*;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;
import org.toop.framework.resource.ResourceLoader;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.MusicAsset;
import org.toop.framework.resource.resources.SoundEffectAsset;

import java.util.Arrays;

public final class Main {
    static void main(String[] args) {
        initSystems();
        App.run(args);
    }

    private static void initSystems() throws NetworkingInitializationException {
        ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/assets"));
        new Thread(NetworkingClientManager::new).start();
        new Thread(() -> {
            MusicManager<MusicAsset> musicManager = new MusicManager<>(MusicAsset.class);
            SoundEffectManager<SoundEffectAsset> soundEffectManager = new SoundEffectManager<>(SoundEffectAsset.class);
                new AudioEventListener<>(
                    musicManager,
                    soundEffectManager,
                    new AudioVolumeManager()
                            .registerManager(VolumeControl.MASTERVOLUME, musicManager)
                            .registerManager(VolumeControl.MASTERVOLUME, soundEffectManager)
                            .registerManager(VolumeControl.FX, soundEffectManager)
                            .registerManager(VolumeControl.MUSIC, musicManager)
                ).initListeners("medium-button-click.wav");
        }).start();
    }
}
