package org.toop.framework.resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.resource.exceptions.ResourceNotFoundException;
import org.toop.framework.resource.resources.*;

/**
 * Centralized manager for all loaded assets in the application.
 *
 * <p>{@code ResourceManager} maintains a thread-safe registry of {@link ResourceMeta} objects and
 * provides utility methods to retrieve assets by name, ID, or type. It works together with {@link
 * ResourceLoader} to register assets automatically when they are loaded from the file system.
 *
 * <p>Key responsibilities:
 *
 * <ul>
 *   <li>Storing all loaded assets in a concurrent map.
 *   <li>Providing typed access to asset resources.
 *   <li>Allowing lookup by asset name or ID.
 *   <li>Supporting retrieval of all assets of a specific {@link BaseResource} subclass.
 * </ul>
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * // Load assets from a loader
 * ResourceLoader loader = new ResourceLoader(new File("RootFolder"));
 * ResourceManager.loadAssets(loader);
 *
 * // Retrieve a single resource
 * ImageAsset background = ResourceManager.get("background.jpg");
 *
 * // Retrieve all fonts
 * List<Asset<FontAsset>> fonts = ResourceManager.getAllOfType(FontAsset.class);
 *
 * // Retrieve by asset name or optional lookup
 * Optional<Asset<? extends BaseResource>> maybeAsset = ResourceManager.findByName("menu.css");
 * }</pre>
 *
 * <p>Notes:
 *
 * <ul>
 *   <li>All retrieval methods are static and thread-safe.
 *   <li>The {@link #get(String)} method may require casting if the asset type is not known at
 *       compile time.
 *   <li>Assets should be loaded via {@link ResourceLoader} before retrieval.
 * </ul>
 */
public class ResourceManager {
    private static final Logger logger = LogManager.getLogger(ResourceManager.class);
    private static final Map<String, ResourceMeta<? extends BaseResource>> assets =
            new ConcurrentHashMap<>();
    private static ResourceManager instance;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    /**
     * Loads all assets from a given {@link ResourceLoader} into the manager.
     *
     * @param loader the loader that has already loaded assets
     */
    public static synchronized void loadAssets(ResourceLoader loader) {
        for (ResourceMeta<? extends BaseResource> asset : loader.getAssets()) {
            assets.put(asset.getName(), asset);
        }
    }

    /**
     * Retrieve the resource of a given name, cast to the expected type.
     *
     * @param name the asset name
     * @param <T> the expected resource type
     * @return the resource, or null if not found
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseResource> T get(String name) {
        ResourceMeta<T> asset = (ResourceMeta<T>) assets.get(name);
        if (asset == null) {
            throw new ResourceNotFoundException(name);
        }
        return asset.getResource();
    }

    /**
     * Retrieve all assets of a specific resource type.
     *
     * @param type the class type to filter
     * @param <T> the resource type
     * @return a list of assets matching the type
     */
    public static <T extends BaseResource> List<ResourceMeta<T>> getAllOfType(Class<T> type) {
        List<ResourceMeta<T>> result = new ArrayList<>();

        for (ResourceMeta<? extends BaseResource> meta : assets.values()) {
            BaseResource res = meta.getResource();
            if (type.isInstance(res)) {
                @SuppressWarnings("unchecked")
                ResourceMeta<T> typed = (ResourceMeta<T>) meta;
                result.add(typed);
            }
        }

        return result;
    }

    public static <T extends BaseResource> List<T> getAllOfTypeAndRemoveWrapper(Class<T> type) {
        List<T> result = new ArrayList<>();

        for (ResourceMeta<? extends BaseResource> meta : assets.values()) {
            BaseResource res = meta.getResource();
            if (type.isInstance(res)) {
                result.add((T) res);
            }
        }

        return result;
    }

    /**
     * Retrieve an asset by its unique ID.
     *
     * @param id the asset ID
     * @return the asset, or null if not found
     */
    public static ResourceMeta<? extends BaseResource> getById(String id) {
        for (ResourceMeta<? extends BaseResource> asset : assets.values()) {
            if (asset.getId().toString().equals(id)) {
                return asset;
            }
        }
        return null;
    }

    /**
     * Add a new asset to the manager.
     *
     * @param asset the asset to add
     */
    public static void addAsset(ResourceMeta<? extends BaseResource> asset) {
        assets.put(asset.getName(), asset);
        logger.info("Successfully added asset: {}, to the asset list", asset.getName());
    }
}
