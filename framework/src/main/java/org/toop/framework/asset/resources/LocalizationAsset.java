package org.toop.framework.asset.resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@FileExtension({"properties"})
public class LocalizationAsset extends BaseResource implements LoadableResource {
    private final Map<Locale, ResourceBundle> bundles = new HashMap<>();
    private boolean isLoaded = false;

    public LocalizationAsset(File file) {
        super(file);
    }

    @Override
    public void load() {
        // Convention: file names like messages_en.properties, ui_de.properties, etc.
        String baseName = getBaseName(getFile().getName());

        // Scan the parent folder for all matching *.properties with same basename
        File folder = getFile().getParentFile();
        File[] files = folder.listFiles((dir, name) ->
                name.startsWith(baseName) && name.endsWith(".properties"));

        if (files != null) {
            for (File f : files) {
                Locale locale = extractLocale(f.getName(), baseName);
                try (InputStreamReader reader =
                             new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
                    this.bundles.put(locale, new PropertyResourceBundle(reader));
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load localization file: " + f, e);
                }
            }
        }

        this.isLoaded = true;
    }

    @Override
    public void unload() {
        this.bundles.clear();
        this.isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    public String getString(String key, Locale locale) {
        ResourceBundle bundle = this.bundles.get(locale);
        if (bundle == null) throw new MissingResourceException(
                "No bundle for locale: " + locale, getClass().getName(), key);
        return bundle.getString(key);
    }

    public boolean hasLocale(Locale locale) {
        return this.bundles.containsKey(locale);
    }

    public Set<Locale> getAvailableLocales() {
        return Collections.unmodifiableSet(this.bundles.keySet());
    }

    private String getBaseName(String fileName) {
        int underscoreIndex = fileName.indexOf('_');
        int dotIndex = fileName.lastIndexOf('.');
        if (underscoreIndex > 0) {
            return fileName.substring(0, underscoreIndex);
        }
        return fileName.substring(0, dotIndex);
    }

    private Locale extractLocale(String fileName, String baseName) {
        int underscoreIndex = fileName.indexOf('_');
        int dotIndex = fileName.lastIndexOf('.');
        if (underscoreIndex > 0 && dotIndex > underscoreIndex) {
            String localePart = fileName.substring(underscoreIndex + 1, dotIndex);
            return Locale.forLanguageTag(localePart.replace('_', '-'));
        }
        return Locale.getDefault(); // fallback
    }
}