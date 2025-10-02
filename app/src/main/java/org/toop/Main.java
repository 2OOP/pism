package org.toop;

import org.toop.app.App;
import org.toop.framework.asset.AssetLoader;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.audio.SoundManager;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;

public final class Main {
	static void main(String[] args) {
		initSystems();
		App.run(args);
	}

	private static void initSystems() throws NetworkingInitializationException {
		AssetManager.loadAssets(new AssetLoader("app/src/main/resources/assets"));
		new Thread(NetworkingClientManager::new).start();
		new Thread(SoundManager::new).start();
	}
}
