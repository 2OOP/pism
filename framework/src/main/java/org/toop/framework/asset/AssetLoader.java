package org.toop.framework.asset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.asset.events.AssetEvents;
import org.toop.framework.asset.resources.*;
import org.toop.framework.eventbus.EventFlow;
import org.reflections.Reflections;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class AssetLoader {
    private static final Logger logger = LogManager.getLogger(AssetLoader.class);
    private static final List<Asset<? extends BaseResource>> assets = new CopyOnWriteArrayList<>();
    private final Map<String, Function<File, ? extends BaseResource>> registry = new ConcurrentHashMap<>();

    private final AtomicInteger loadedCount = new AtomicInteger(0);
    private int totalCount = 0;

    public AssetLoader(File rootFolder) {
        autoRegisterResources();
        List<File> foundFiles = new ArrayList<>();
        fileSearcher(rootFolder, foundFiles);
        this.totalCount = foundFiles.size();
        loader(foundFiles);
    }

    public AssetLoader(String rootFolder) {
        this(new File(rootFolder));
    }

    public double getProgress() {
        return (totalCount == 0) ? 1.0 : (loadedCount.get() / (double) totalCount);
    }

    public int getLoadedCount() {
        return loadedCount.get();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<Asset<? extends BaseResource>> getAssets() {
        return new ArrayList<>(assets);
    }

    public <T extends BaseResource> void register(String extension, Function<File, T> factory) {
        this.registry.put(extension, factory);
    }

    private <T extends BaseResource> T resourceMapper(File file, Class<T> type) {
        String ext = getExtension(file.getName());
        Function<File, ? extends BaseResource> factory = registry.get(ext);
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
        Map<String, BundledResource> bundledResources = new HashMap<>();

        for (File file : files) {
            BaseResource resource = resourceMapper(file, BaseResource.class);
            switch (resource) {
                case null -> {
                    continue;
                }
                case BundledResource br -> {
                    String key = resource.getClass().getName() + ":" + br.getBaseName();
                    if (bundledResources.containsKey(key)) {
                        bundledResources.get(key).loadFile(file);
                        resource = (BaseResource) bundledResources.get(key);
                    } else {
                        br.loadFile(file);
                        bundledResources.put(key, br);
                    }
                }
                case FontAsset fontAsset -> fontAsset.load();
                default -> {
                }
            }

            BaseResource finalResource = resource;
            boolean alreadyAdded = assets.stream()
                    .anyMatch(a -> a.getResource() == finalResource);
            if (!alreadyAdded) {
                assets.add(new Asset<>(file.getName(), resource));
            }

            logger.info("Loaded {} from {}", resource.getClass().getSimpleName(), file.getAbsolutePath());
            loadedCount.incrementAndGet();
            new EventFlow()
                    .addPostEvent(new AssetEvents.LoadingProgressUpdate(loadedCount.get(), totalCount))
                    .postEvent();
        }
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
                registry.put(ext, file -> {
                    try {
                        return cls.getConstructor(File.class).newInstance(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    private static String getBaseName(String fileName) {
        int underscoreIndex = fileName.indexOf('_');
        int dotIndex = fileName.lastIndexOf('.');
        if (underscoreIndex > 0) return fileName.substring(0, underscoreIndex);
        return fileName.substring(0, dotIndex);
    }

    public static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }
}
