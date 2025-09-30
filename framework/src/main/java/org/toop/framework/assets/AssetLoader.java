package org.toop.framework.assets;

import org.toop.framework.assets.resources.AudioResource;
import org.toop.framework.assets.resources.FontResource;
import org.toop.framework.assets.resources.ImageResource;
import org.toop.framework.assets.resources.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class AssetLoader {
    private final File rootFolder;
    private final ArrayList<Asset<Resource>> assets = new ArrayList<>();

    public AssetLoader(File rootFolder) {
        this.rootFolder = rootFolder;
        fileSearcher(rootFolder);
    }

    public File getRootFolder() {
        return this.rootFolder;
    }

    public ArrayList<Asset<Resource>> getAssets() {
        return this.assets;
    }

    private Resource resourceMapper(File file) throws FileNotFoundException {
        return switch (getExtension(file.getName())) {
            case "wav" -> new AudioResource(file).load();
            case "png" -> new ImageResource(file).load();
            default -> null;
        };
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
                    this.assets.add(
                            new Asset<>(fileEntry.getName(), this.resourceMapper(fileEntry))
                    );
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
