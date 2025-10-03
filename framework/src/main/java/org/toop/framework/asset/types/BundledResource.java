package org.toop.framework.asset.types;

import org.toop.framework.asset.ResourceLoader;

import java.io.File;

/**
 * Represents a resource that can be composed of multiple files, or "bundled" together
 * under a common base name.
 *
 * <p>Implementing classes allow an {@link ResourceLoader}
 * to automatically merge multiple related files into a single resource instance.</p>
 *
 * <p>Typical use cases include:</p>
 * <ul>
 *     <li>Localization assets, where multiple `.properties` files (e.g., `messages_en.properties`,
 *     `messages_nl.properties`) are grouped under the same logical resource.</li>
 *     <li>Sprite sheets, tile sets, or other multi-file resources that logically belong together.</li>
 * </ul>
 *
 * <p>Implementing classes must provide:</p>
 * <ul>
 *     <li>{@link #loadFile(File)}: Logic to load or merge an individual file into the resource.</li>
 *     <li>{@link #getBaseName()}: A consistent base name used to group multiple files into this resource.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class LocalizationAsset extends BaseResource implements BundledResource {
 *     private final String baseName;
 *
 *     public LocalizationAsset(File file) {
 *         super(file);
 *         this.baseName = extractBaseName(file.getName());
 *         loadFile(file);
 *     }
 *
 *     @Override
 *     public void loadFile(File file) {
 *         // merge file into existing bundles
 *     }
 *
 *     @Override
 *     public String getBaseName() {
 *         return baseName;
 *     }
 * }
 * }</pre>
 *
 * <p>When used with an asset loader, all files sharing the same base name are
 * automatically merged into a single resource instance.</p>
 */
public interface BundledResource {

    /**
     * Load or merge an additional file into this resource.
     *
     * @param file the file to load or merge
     */
    void loadFile(File file);

    /**
     * Return a base name for grouping multiple files into this single resource.
     * Files with the same base name are automatically merged by the loader.
     *
     * @return the base name used to identify this bundled resource
     */
    String getBaseName();

//    /**
//    Returns the name
//    */
//    String getDefaultName();
}