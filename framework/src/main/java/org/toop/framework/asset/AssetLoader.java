package org.toop.framework.asset;

import org.toop.framework.asset.resources.BaseResource;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.reflections.Reflections;
import org.toop.framework.asset.resources.FileExtension;

public class AssetLoader {

    private final List<Asset<? extends BaseResource>> assets = new CopyOnWriteArrayList<>();
    private final Map<String, Function<File, ? extends BaseResource>> registry = new ConcurrentHashMap<>();

    public AssetLoader(File rootFolder) {
        autoRegisterResources();
        fileSearcher(rootFolder);
    }

    public AssetLoader(String rootFolder) {
        this(new File(rootFolder));
    }

    public List<Asset<? extends BaseResource>> getAssets() {
        return new ArrayList<>(assets);
    }

    public <T extends BaseResource> void register(String extension, Function<File, T> factory) {
        registry.put(extension, factory);
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

    private void fileSearcher(final File folder) {
        for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                fileSearcher(fileEntry);
            } else {
                BaseResource resource = resourceMapper(fileEntry, BaseResource.class);
                if (resource != null) {
                    assets.add(new Asset<>(fileEntry.getName(), resource));
                }
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

    public static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }
}
