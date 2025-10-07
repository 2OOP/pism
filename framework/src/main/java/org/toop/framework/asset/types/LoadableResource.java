package org.toop.framework.asset.types;

import org.toop.framework.asset.ResourceLoader;

/**
 * Represents a resource that can be explicitly loaded and unloaded.
 *
 * <p>Any class implementing {@code LoadableResource} is responsible for managing its own loading
 * and unloading logic, such as reading files, initializing data structures, or allocating external
 * resources.
 *
 * <p>Implementing classes must define the following behaviors:
 *
 * <ul>
 *   <li>{@link #load()}: Load the resource into memory or perform necessary initialization.
 *   <li>{@link #unload()}: Release any held resources or memory when the resource is no longer
 *       needed.
 *   <li>{@link #isLoaded()}: Return {@code true} if the resource has been successfully loaded and
 *       is ready for use, {@code false} otherwise.
 * </ul>
 *
 * <p>Typical usage:
 *
 * <pre>{@code
 * public class MyFontAsset extends BaseResource implements LoadableResource {
 *     private boolean loaded = false;
 *
 *     @Override
 *     public void load() {
 *         // Load font file into memory
 *         loaded = true;
 *     }
 *
 *     @Override
 *     public void unload() {
 *         // Release resources if needed
 *         loaded = false;
 *     }
 *
 *     @Override
 *     public boolean isLoaded() {
 *         return loaded;
 *     }
 * }
 * }</pre>
 *
 * <p>This interface is commonly used with {@link PreloadResource} to allow automatic loading by an
 * {@link ResourceLoader} if desired.
 */
public interface LoadableResource {
    /**
     * Load the resource into memory or initialize it. This method may throw runtime exceptions if
     * loading fails.
     */
    void load();

    /**
     * Unload the resource and free any associated resources. After this call, {@link #isLoaded()}
     * should return false.
     */
    void unload();

    /**
     * Check whether the resource has been successfully loaded.
     *
     * @return true if the resource is loaded and ready for use, false otherwise
     */
    boolean isLoaded();
}
