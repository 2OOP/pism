package org.toop.framework.resource;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.events.AssetLoaderEvents;
import org.toop.framework.resource.exceptions.CouldNotCreateResourceFactoryException;
import org.toop.framework.resource.resources.*;
import org.toop.framework.resource.types.BundledResource;
import org.toop.framework.resource.types.FileExtension;
import org.toop.framework.resource.types.PreloadResource;

/**
 * Responsible for loading assets from a file system directory into memory.
 *
 * <p>The {@code ResourceLoader} scans a root folder recursively, identifies files, and maps them to
 * registered resource types based on file extensions and {@link FileExtension} annotations. It
 * supports multiple resource types including {@link PreloadResource} (automatically loaded) and
 * {@link BundledResource} (merged across multiple files).
 *
 * <p>Assets are stored in a static, thread-safe list and can be retrieved through {@link
 * ResourceManager}.
 *
 * <p>Features:
 *
 * <ul>
 *   <li>Recursive directory scanning for assets.
 *   <li>Automatic registration of resource classes via reflection.
 *   <li>Bundled resource support: multiple files merged into a single resource instance.
 *   <li>Preload resources automatically invoke {@link PreloadResource#load()}.
 *   <li>Progress tracking via {@link AssetLoaderEvents.LoadingProgressUpdate} events.
 * </ul>
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * ResourceLoader loader = new ResourceLoader("assets");
 * double progress = loader.getProgress();
 * List<Asset<? extends BaseResource>> loadedAssets = loader.getAssets();
 * }</pre>
 */
public class ResourceLoader {
    private static final Logger logger = LogManager.getLogger(ResourceLoader.class);
    private static final List<ResourceMeta<? extends BaseResource>> assets =
            new CopyOnWriteArrayList<>();
    private final Map<String, Function<File, ? extends BaseResource>> registry =
            new ConcurrentHashMap<>();

    private final AtomicInteger loadedCount = new AtomicInteger(0);
    private int totalCount = 0;

    /**
     * Constructs an ResourceLoader and loads assets from the given root folder.
     *
     * @param rootFolder the folder containing asset files
     */
    public ResourceLoader(File rootFolder) {
        autoRegisterResources();
        List<File> foundFiles = new ArrayList<>();
        fileSearcher(rootFolder, foundFiles);
        this.totalCount = foundFiles.size();

        // measure memory before loading
        long before = getUsedMemory();

        loader(foundFiles);

        // ~measure memory after loading
        long after = getUsedMemory();
        long used = after - before;

        logger.info("Total files loaded: {}", this.totalCount);
        logger.info("Memory used by assets: ~{} MB", used / (1024 * 1024));
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Constructs an ResourceLoader from a folder path.
     *
     * @param rootFolder the folder path containing assets
     */
    public ResourceLoader(String rootFolder) {
        this(new File(rootFolder));
    }

    /**
     * Returns the current progress of loading assets (0.0 to 1.0).
     *
     * @return progress as a double
     */
    public double getProgress() {
        return (totalCount == 0) ? 1.0 : (loadedCount.get() / (double) totalCount);
    }

    /**
     * Returns the number of assets loaded so far.
     *
     * @return loaded count
     */
    public int getLoadedCount() {
        return loadedCount.get();
    }

    /**
     * Returns the total number of files found to load.
     *
     * @return total asset count
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * Returns a snapshot list of all assets loaded by this loader.
     *
     * @return list of loaded assets
     */
    public List<ResourceMeta<? extends BaseResource>> getAssets() {
        return new ArrayList<>(assets);
    }

    /**
     * Registers a factory for a specific file extension.
     *
     * @param extension the file extension (without dot)
     * @param factory a function mapping a File to a resource instance
     * @param <T> the type of resource
     */
    public <T extends BaseResource> void register(String extension, Function<File, T> factory) {
        this.registry.put(extension, factory);
    }

    /** Maps a file to a resource instance based on its extension and registered factories. */
    private <T extends BaseResource> T resourceMapper(File file)
            throws CouldNotCreateResourceFactoryException, IllegalArgumentException {
        String ext = getExtension(file.getName());
        Function<File, ? extends BaseResource> factory = registry.get(ext);
        if (factory == null)
            throw new CouldNotCreateResourceFactoryException(registry, file.getName());

        BaseResource resource = factory.apply(file);

        if (resource == null) {
            throw new IllegalArgumentException(
                    "File "
                            + file.getName()
                            + " is not of type "
                            + BaseResource.class.getSimpleName());
        }

        return ((Class<T>) BaseResource.class).cast(resource);
    }

    /** Loads the given list of files into assets, handling bundled and preload resources. */
    private void loader(List<File> files) {
        Map<String, BundledResource> bundledResources = new HashMap<>();

        for (File file : files) {
            boolean skipAdd = false;
            BaseResource resource = null;
            try {
                resource = resourceMapper(file);
            } catch (CouldNotCreateResourceFactoryException _) {
                logger.warn("Could not create resource for: {}", file);
            } catch (IllegalArgumentException e) {
                logger.error(e);
            }
            switch (resource) {
                case null -> {
                    continue;
                }
                case BundledResource br -> {
                    String key = resource.getClass().getName() + ":" + br.getBaseName();
                    if (!bundledResources.containsKey(key)) {
                        bundledResources.put(key, br);
                    }
                    bundledResources.get(key).loadFile(file);
                    resource = (BaseResource) bundledResources.get(key);
                    assets.add(new ResourceMeta<>(br.getBaseName(), resource));
                    skipAdd = true;
                }
                case PreloadResource pr -> pr.load();
                default -> {}
            }

            BaseResource finalResource = resource;
            boolean alreadyAdded = assets.stream().anyMatch(a -> a.getResource() == finalResource);
            if (!alreadyAdded && !skipAdd) {
                assets.add(new ResourceMeta<>(file.getName(), resource));
            }

            logger.info(
                    "Loaded {} from {}",
                    resource.getClass().getSimpleName(),
                    file.getAbsolutePath());
            loadedCount.incrementAndGet();
            new EventFlow()
                    .addPostEvent(
                            new AssetLoaderEvents.LoadingProgressUpdate(
                                    loadedCount.get(), totalCount))
                    .postEvent();
        }
    }

    /** Recursively searches a folder and adds all files to the foundFiles list. */
    private void fileSearcher(final File folder, List<File> foundFiles) {
        for (File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                fileSearcher(fileEntry, foundFiles);
            } else {
                foundFiles.add(fileEntry);
            }
        }
    }

    /**
     * Uses reflection to automatically register all {@link BaseResource} subclasses annotated with
     * {@link FileExtension}.
     */
    private void autoRegisterResources() {
        Reflections reflections = new Reflections("org.toop.framework.resource.resources");
        Set<Class<? extends BaseResource>> classes = reflections.getSubTypesOf(BaseResource.class);

        for (Class<? extends BaseResource> cls : classes) {
            if (!cls.isAnnotationPresent(FileExtension.class)) continue;
            FileExtension annotation = cls.getAnnotation(FileExtension.class);
            for (String ext : annotation.value()) {
                registry.put(
                        ext,
                        file -> {
                            try {
                                return cls.getConstructor(File.class).newInstance(file);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }
    }

    /** Extracts the base name from a file name, used for bundling multiple files. */
    private static String getBaseName(String fileName) {
        int underscoreIndex = fileName.indexOf('_');
        int dotIndex = fileName.lastIndexOf('.');
        if (underscoreIndex > 0) return fileName.substring(0, underscoreIndex);
        return fileName.substring(0, dotIndex);
    }

    /** Returns the file extension of a given file name (without dot). */
    public static String getExtension(String name) {
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1) : "";
    }
}
