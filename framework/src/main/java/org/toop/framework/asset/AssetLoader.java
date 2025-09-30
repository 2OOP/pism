package org.toop.framework.asset;

import org.toop.framework.asset.resources.AudioAsset;
import org.toop.framework.asset.resources.BaseResource;
import org.toop.framework.asset.resources.ImageAsset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class AssetLoader {
    private final File rootFolder;
    private final ArrayList<Asset<? extends BaseResource>> assets = new ArrayList<>();

    public AssetLoader(File rootFolder) {
        this.rootFolder = rootFolder;
        fileSearcher(rootFolder);
    }

    public AssetLoader(String rootFolder) {
        this.rootFolder = new File(rootFolder);
        fileSearcher(this.rootFolder);
    }

    public File getRootFolder() {
        return this.rootFolder;
    }

    public ArrayList<Asset<? extends BaseResource>> getAssets() {
        return this.assets;
    }

    private <T extends BaseResource> T resourceMapper(Class<T> type, File file) throws FileNotFoundException {
        BaseResource resource = switch (getExtension(file.getName())) {
            case "wav" -> new AudioAsset(file);
            case "png" -> new ImageAsset(file);
            default -> null;
        };

        if (resource == null) return null;
        if (!type.isInstance(resource))
            throw new IllegalArgumentException("File " + file.getName() + " is not of type " + type);
        return type.cast(resource);
    }

    public static String getExtension(String name) {
        String extension = "";

        int i = name.lastIndexOf('.');
        if (i > 0) {
            extension = name.substring(i+1);
        }
        return extension;
    }

    private void fileSearcher(final File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                fileSearcher(fileEntry);
            } else {
                try {
                    BaseResource resource = resourceMapper(BaseResource.class, fileEntry); // generic token
                    if (resource != null) {
                        this.assets.add(new Asset<>(fileEntry.getName(), resource));
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
