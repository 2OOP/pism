package org.toop.framework.asset;

import org.toop.framework.asset.resources.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Centralized manager for all loaded assets in the application.
 * <p>
 * {@code AssetManager} maintains a thread-safe registry of {@link Asset} objects
 * and provides utility methods to retrieve assets by name, ID, or type.
 * It works together with {@link AssetLoader} to register assets automatically
 * when they are loaded from the file system.
 * </p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *     <li>Storing all loaded assets in a concurrent map.</li>
 *     <li>Providing typed access to asset resources.</li>
 *     <li>Allowing lookup by asset name or ID.</li>
 *     <li>Supporting retrieval of all assets of a specific {@link BaseResource} subclass.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Load assets from a loader
 * AssetLoader loader = new AssetLoader(new File("RootFolder"));
 * AssetManager.loadAssets(loader);
 *
 * // Retrieve a single resource
 * ImageAsset background = AssetManager.get("background.jpg");
 *
 * // Retrieve all fonts
 * List<Asset<FontAsset>> fonts = AssetManager.getAllOfType(FontAsset.class);
 *
 * // Retrieve by asset name or optional lookup
 * Optional<Asset<? extends BaseResource>> maybeAsset = AssetManager.findByName("menu.css");
 * }</pre>
 *
 * <p>Notes:</p>
 * <ul>
 *     <li>All retrieval methods are static and thread-safe.</li>
 *     <li>The {@link #get(String)} method may require casting if the asset type is not known at compile time.</li>
 *     <li>Assets should be loaded via {@link AssetLoader} before retrieval.</li>
 * </ul>
 */
public class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();
    private static final Map<String, Asset<? extends BaseResource>> assets = new ConcurrentHashMap<>();

    private AssetManager() {}

    /**
     * Returns the singleton instance of {@code AssetManager}.
     *
     * @return the shared instance
     */
    public static AssetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Loads all assets from a given {@link AssetLoader} into the manager.
     *
     * @param loader the loader that has already loaded assets
     */
    public synchronized static void loadAssets(AssetLoader loader) {
        for (var asset : loader.getAssets()) {
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
        Asset<T> asset = (Asset<T>) assets.get(name);
        if (asset == null) return null;
        return asset.getResource();
    }

    /**
     * Retrieve all assets of a specific resource type.
     *
     * @param type the class type to filter
     * @param <T> the resource type
     * @return a list of assets matching the type
     */
    public static <T extends BaseResource> ArrayList<Asset<T>> getAllOfType(Class<T> type) {
        ArrayList<Asset<T>> list = new ArrayList<>();
        for (Asset<? extends BaseResource> asset : assets.values()) {
            if (type.isInstance(asset.getResource())) {
                @SuppressWarnings("unchecked")
                Asset<T> typed = (Asset<T>) asset;
                list.add(typed);
            }
        }
        return list;
    }

    /**
     * Retrieve an asset by its unique ID.
     *
     * @param id the asset ID
     * @return the asset, or null if not found
     */
    public static Asset<? extends BaseResource> getById(String id) {
        for (Asset<? extends BaseResource> asset : assets.values()) {
            if (asset.getId().toString().equals(id)) {
                return asset;
            }
        }
        return null;
    }

    /**
     * Retrieve an asset by its name.
     *
     * @param name the asset name
     * @return the asset, or null if not found
     */
    public static Asset<? extends BaseResource> getByName(String name) {
        return assets.get(name);
    }

    /**
     * Attempt to find an asset by name, returning an {@link Optional}.
     *
     * @param name the asset name
     * @return an Optional containing the asset if found
     */
    public static Optional<Asset<? extends BaseResource>> findByName(String name) {
        return Optional.ofNullable(assets.get(name));
    }

    /**
     * Add a new asset to the manager.
     *
     * @param asset the asset to add
     */
    public static void addAsset(Asset<? extends BaseResource> asset) {
        assets.put(asset.getName(), asset);
    }
}