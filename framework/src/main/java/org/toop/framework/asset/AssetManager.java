package org.toop.framework.asset;

import org.toop.framework.asset.resources.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();
    private static final Map<String, Asset<? extends BaseResource>> assets = new ConcurrentHashMap<>();

    private AssetManager() {}

    public static AssetManager getInstance() {
        return INSTANCE;
    }

    public synchronized static void loadAssets(AssetLoader loader) {
        for (var asset : loader.getAssets()) {
            assets.put(asset.getName(), asset);
        }
    }

    public static <T extends BaseResource> ArrayList<Asset<T>> getAllOfType(Class<T> type) {
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

    public static Asset<? extends BaseResource> getById(String id) {
        for (Asset<? extends BaseResource> asset : assets.values()) {
            if (asset.getId().toString().equals(id)) {
                return asset;
            }
        }
        return null;
    }

    public static Asset<? extends BaseResource> getByName(String name) {
        return assets.get(name);
    }

    public static Optional<Asset<? extends BaseResource>> findByName(String name) {
        return Optional.ofNullable(assets.get(name));
    }

    public static void addAsset(Asset<? extends BaseResource> asset) {
        assets.put(asset.getName(), asset);
    }

}