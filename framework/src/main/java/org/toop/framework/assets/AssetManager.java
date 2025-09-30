package org.toop.framework.assets;

import org.apache.maven.surefire.shared.io.function.IOBaseStream;
import org.toop.framework.assets.resources.Resource;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;

public class AssetManager {

    private final HashMap<String, Asset<Resource>> assets = new HashMap<>();

    public AssetManager(File rootFolder) {
        for (Asset<Resource> x : new AssetLoader(rootFolder).getAssets()) {
            this.assets.put(x.getName(), x);
        }
    }

    public <T extends Resource> HashMap<String, Asset<T>> getAllResourceOfType(Class<T> resourceClass) {
        HashMap<String, Asset<T>> a = new HashMap<>();
        for (Asset<Resource> b : this.assets.values()) {
            if (resourceClass.isInstance(b.getResource())) {
                a.put(b.getName(), (Asset<T>) b);
            }
        }
        return a;
    }
    
    public HashMap<String, Asset<Resource>> getAssets() {
        return this.assets;
    }

    public Asset<Resource> getAsset(String assetName) {
        return assets.get(assetName);
    }
    
}
