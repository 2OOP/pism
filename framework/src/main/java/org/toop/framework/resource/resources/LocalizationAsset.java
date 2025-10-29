package org.toop.framework.resource.resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.toop.framework.resource.types.BundledResource;
import org.toop.framework.resource.types.FileExtension;
import org.toop.framework.resource.types.LoadableResource;

/**
 * Represents a localization resource asset that loads and manages property files containing
 * key-value pairs for different locales.
 *
 * <p>This class implements {@link LoadableResource} to support loading/unloading and {@link
 * BundledResource} to represent resources that can contain multiple localized bundles.
 *
 * <p>Files handled by this class must have the {@code .properties} extension, optionally with a
 * locale suffix, e.g., {@code messages_en_US.properties}.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * LocalizationAsset asset = new LocalizationAsset(new File("messages_en_US.properties"));
 * asset.load();
 * String greeting = asset.getString("hello", Locale.US);
 * }</pre>
 */
@FileExtension({"properties"})
public class LocalizationAsset extends BaseResource implements LoadableResource, BundledResource {

    /** Map of loaded resource bundles, keyed by locale. */
    private final Map<Locale, ResourceBundle> bundles = new HashMap<>();

    /** Flag indicating whether this asset has been loaded. */
    private boolean loaded = false;

    /** Basename of the given asset */
    private final String baseName = "localization";

    /** Fallback locale used when no matching locale is found. */
    private final Locale fallback = Locale.forLanguageTag("en");

    /**
     * Constructs a new LocalizationAsset for the specified file.
     *
     * @param file the property file to load
     */
    public LocalizationAsset(File file) {
        super(file);
    }

    /** Loads the resource file into memory and prepares localized bundles. */
    @Override
    public void load() {
        loadFile(getFile());
        loaded = true;
    }

    /** Unloads all loaded resource bundles, freeing memory. */
    @Override
    public void unload() {
        bundles.clear();
        loaded = false;
    }

    /** Returns the fallback locale used when locale is missing argument*/
    public Locale getFallback() {return this.fallback;}

    /**
     * Checks whether this asset has been loaded.
     *
     * @return {@code true} if the asset is loaded, {@code false} otherwise
     */
    @Override
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Retrieves a localized string for the given key and locale. If an exact match for the locale
     * is not found, a fallback matching the language or the default locale will be used.
     *
     * @param key the key of the string
     * @param locale the desired locale
     * @return the localized string
     * @throws MissingResourceException if no resource bundle is available for the locale
     */
    public String getString(String key, Locale locale) {
        Locale target = findBestLocale(locale);
        ResourceBundle bundle = bundles.get(target);
        if (bundle == null)
            throw new MissingResourceException(
                    "No bundle for locale: " + target, getClass().getName(), key);
        return bundle.getString(key);
    }

    /**
     * Finds the best matching locale among loaded bundles. Prefers an exact match, then
     * language-only match, then fallback.
     *
     * @param locale the desired locale
     * @return the best matching locale
     */
    private Locale findBestLocale(Locale locale) {
        if (bundles.containsKey(locale)) return locale;
        for (Locale l : bundles.keySet()) {
            if (l.getLanguage().equals(locale.getLanguage())) return l;
        }
        return fallback;
    }

    /**
     * Returns an unmodifiable set of all locales for which bundles are loaded.
     *
     * @return available locales
     */
    public Set<Locale> getAvailableLocales() {
        return Collections.unmodifiableSet(bundles.keySet());
    }

    /**
     * Loads a specific property file as a resource bundle. The locale is extracted from the file
     * name if present.
     *
     * @param file the property file to load
     * @throws RuntimeException if the file cannot be read
     */
    @Override
    public void loadFile(File file) {
        try (InputStreamReader reader =
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Locale locale = extractLocale(file.getName(), baseName);
            bundles.put(locale, new PropertyResourceBundle(reader));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load localization file: " + file, e);
        }
        loaded = true;
    }

    /**
     * Returns the base name of the underlying file (without locale or extension).
     *
     * @return the base name
     */
    @Override
    public String getBaseName() {
        return this.baseName;
    }

    //    /**
    //     * Extracts the base name from a file name.
    //     *
    //     * @param fileName the file name
    //     * @return base name without locale or extension
    //     */
    //    private String getBaseName(String fileName) {
    //        int dotIndex = fileName.lastIndexOf('.');
    //        String nameWithoutExtension = (dotIndex > 0) ? fileName.substring(0, dotIndex) :
    // fileName;
    //
    //        int underscoreIndex = nameWithoutExtension.indexOf('_');
    //        if (underscoreIndex > 0) {
    //            return nameWithoutExtension.substring(0, underscoreIndex);
    //        }
    //        return nameWithoutExtension;
    //    }

    /**
     * Extracts a locale from a file name based on the pattern "base_LOCALE.properties".
     *
     * @param fileName the file name
     * @param baseName the base name
     * @return extracted locale, or fallback if none found
     */
    private Locale extractLocale(String fileName, String baseName) {
        int underscoreIndex = fileName.indexOf('_');
        int dotIndex = fileName.lastIndexOf('.');
        if (underscoreIndex > 0 && dotIndex > underscoreIndex) {
            String localePart = fileName.substring(underscoreIndex + 1, dotIndex);
            return Locale.forLanguageTag(localePart.replace('_', '-'));
        }
        return fallback;
    }
}
