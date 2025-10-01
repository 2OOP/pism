package org.toop.framework.asset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.asset.events.AssetEvents;
import org.toop.framework.asset.resources.BaseResource;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.reflections.Reflections;
import org.toop.framework.asset.resources.FileExtension;
import org.toop.framework.asset.resources.FontAsset;
import org.toop.framework.eventbus.EventFlow;

public class AssetLoader {
    private static final Logger logger = LogManager.getLogger(AssetLoader.class);
    private final List<Asset<? extends BaseResource>> assets = new CopyOnWriteArrayList<>();
    private final Map<String, Function<File, ? extends BaseResource>> registry = new ConcurrentHashMap<>();

    private volatile int loadedCount = 0;
    private volatile int totalCount = 0;

    public AssetLoader(File rootFolder) {
        autoRegisterResources(); // make sure resources are registered!

        List<File> foundFiles = new ArrayList<>();
        fileSearcher(rootFolder, foundFiles);
        this.totalCount = foundFiles.size();
        loader(foundFiles);
    }

    public AssetLoader(String rootFolder) {
        this(new File(rootFolder));
    }

    public double getProgress() {
        return (this.totalCount == 0) ? 1.0 : (this.loadedCount / (double) this.totalCount);
    }

    public int getLoadedCount() {
        return this.loadedCount;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public List<Asset<? extends BaseResource>> getAssets() {
        return new ArrayList<>(this.assets);
    }

    public <T extends BaseResource> void register(String extension, Function<File, T> factory) {
        this.registry.put(extension, factory);
    }

    private <T extends BaseResource> T resourceMapper(File file, Class<T> type) {
        String ext = getExtension(file.getName());
        Function<File, ? extends BaseResource> factory = this.registry.get(ext);

        if (factory == null) return null;

        BaseResource resource = factory.apply(file);

        if (!type.isInstance(resource)) {
            throw new IllegalArgumentException(
                    "File " + file.getName() + " is not of type " + type.getSimpleName()
            );
        }

        return type.cast(resource);
    }

    private void loader(List<File> files) {
        for (File file : files) {
            BaseResource resource = resourceMapper(file, BaseResource.class);
            if (resource != null) {
                Asset<? extends BaseResource> asset = new Asset<>(file.getName(), resource);
                this.assets.add(asset);

                if (resource instanceof FontAsset fontAsset) {
                    fontAsset.load();
                }

                logger.info("Loaded {} from {}", resource.getClass().getSimpleName(), file.getAbsolutePath());

                this.loadedCount++; // TODO: Fix non atmomic operation
                new EventFlow()
                        .addPostEvent(new AssetEvents.LoadingProgressUpdate(this.loadedCount, this.totalCount))
                        .postEvent();
            }
        }
        logger.info("Loaded {} assets", files.size());
    }

    private void fileSearcher(final File folder, List<File> foundFiles) {
        for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                fileSearcher(fileEntry, foundFiles);
            } else {
                foundFiles.add(fileEntry);
            }
        }
    }

    private void autoRegisterResources() {
        Reflections reflections = new Reflections("org.toop.framework.asset.resources");
        Set<Class<? extends BaseResource>> classes = reflections.getSubTypesOf(BaseResource.class);

        for (Class<? extends BaseResource> cls : classes) {
            if (!cls.isAnnotationPresent(FileExtension.class)) continue;
            FileExtension annotation = cls.getAnnotation(FileExtension.class);
            for (String ext : annotation.value()) {
                this.registry.put(ext, file -> {
                    try {
                        return cls.getConstructor(File.class).newInstance(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }
}
