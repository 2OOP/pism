package org.toop;

import org.toop.app.App;
import org.toop.framework.asset.ResourceLoader;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.audio.SoundManager;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;

public final class Main {
    public static void main(String[] args) {
        initSystems();
        App.run(args);
    }

    private static void initSystems() throws NetworkingInitializationException {
        ResourceManager.loadAssets(new ResourceLoader("app/src/main/resources/assets"));
        new Thread(NetworkingClientManager::new).start();
        new Thread(SoundManager::new).start();
    }
}
