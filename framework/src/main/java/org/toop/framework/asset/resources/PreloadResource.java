package org.toop.framework.asset.resources;

/**
 * Marker interface for resources that should be **automatically loaded** by the {@link org.toop.framework.asset.AssetLoader}.
 *
 * <p>Extends {@link LoadableResource}, so any implementing class must provide the standard
 * {@link LoadableResource#load()} and {@link LoadableResource#unload()} methods, as well as the
 * {@link LoadableResource#isLoaded()} check.</p>
 *
 * <p>When a resource implements {@code PreloadResource}, the {@code AssetLoader} will invoke
 * {@link LoadableResource#load()} automatically after the resource is discovered and instantiated,
 * without requiring manual loading by the user.</p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * public class MyFontAsset extends BaseResource implements PreloadResource {
 *     @Override
 *     public void load() {
 *         // load the font into memory
 *     }
 *
 *     @Override
 *     public void unload() {
 *         // release resources if needed
 *     }
 *
 *     @Override
 *     public boolean isLoaded() {
 *         return loaded;
 *     }
 * }
 * }</pre>
 *
 * <p>Note: Only use this interface for resources that are safe to load at startup, as it may
 * increase memory usage or startup time.</p>
 */
public interface PreloadResource extends LoadableResource {}
