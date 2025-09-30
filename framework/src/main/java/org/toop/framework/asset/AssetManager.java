package org.toop.framework.asset;

import org.toop.framework.asset.resources.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();
    private static final Map<String, Asset<? extends BaseResource>> assets = new HashMap<>();

    private AssetManager() {}

    public static AssetManager getInstance() {
        return INSTANCE;
    }

    public static void loadAssets(AssetLoader loader) {
        for (var asset : loader.getAssets()) {
            assets.put(asset.getName(), asset);
        }
    }

    public <T extends BaseResource> ArrayList<Asset<T>> getAllOfType(Class<T> type) {
        ArrayList<Asset<T>> list = new ArrayList<>();
        for (Asset<? extends BaseResource> asset : assets.values()) {  // <-- use .values()
            if (type.isInstance(asset.getResource())) {
                @SuppressWarnings("unchecked")
                Asset<T> typed = (Asset<T>) asset;
                list.add(typed);
            }
        }
        return list;
    }

    public static Asset<? extends BaseResource> getById(String guid) {
        return assets.get(guid);
    }

    public static Optional<Asset<? extends BaseResource>> findByName(String name) {
        return assets.values().stream()
                .filter(a -> a.getName().equals(name))
                .findFirst();
    }
}