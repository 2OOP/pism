package org.toop.framework.asset.resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@FileExtension({"properties"})
public class LocalizationAsset extends BaseResource implements LoadableResource, BundledResource {
    private final Map<Locale, ResourceBundle> bundles = new HashMap<>();
    private boolean isLoaded = false;
    private final Locale fallback = Locale.forLanguageTag("");

    public LocalizationAsset(File file) {
        super(file);
    }

    @Override
    public void load() {
        loadFile(getFile());
        isLoaded = true;
    }

    @Override
    public void unload() {
        bundles.clear();
        isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    public String getString(String key, Locale locale) {
        Locale target = findBestLocale(locale);
        ResourceBundle bundle = bundles.get(target);
        if (bundle == null) throw new MissingResourceException(
                "No bundle for locale: " + target, getClass().getName(), key);
        return bundle.getString(key);
    }

    private Locale findBestLocale(Locale locale) {
        if (bundles.containsKey(locale)) return locale;
        for (Locale l : bundles.keySet()) {
            if (l.getLanguage().equals(locale.getLanguage())) return l;
        }
        return fallback;
    }

    public Set<Locale> getAvailableLocales() {
        return Collections.unmodifiableSet(bundles.keySet());
    }

    @Override
    public void loadFile(File file) {
        String baseName = getBaseName(file.getName());
        try (InputStreamReader reader =
                     new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            Locale locale = extractLocale(file.getName(), baseName);
            bundles.put(locale, new PropertyResourceBundle(reader));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load localization file: " + file, e);
        }
        isLoaded = true;
    }

    @Override
    public String getBaseName() {
        return getBaseName(getFile().getName());
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
        return fallback;
    }
}
