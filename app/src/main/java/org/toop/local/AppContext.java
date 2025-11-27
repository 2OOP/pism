package org.toop.local;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.LocalizationAsset;

import java.util.Locale;
import java.util.MissingResourceException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppContext {
    private static final LocalizationAsset localization = ResourceManager.get("localization");
    private static Locale locale = Locale.forLanguageTag("en");

    private static final Logger logger = LogManager.getLogger(AppContext.class);

	private static final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(locale);

    public static LocalizationAsset getLocalization() {
        return localization;
    }

    public static void setLocale(Locale locale) {
        AppContext.locale = locale;
		localeProperty.set(locale);
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String getString(String key) {
        assert localization != null;

        // TODO: Gebruik ResourceBundle.getBundle() zodat de fallback automatisch gaat.
        //          Hiervoor zou de assetManager aangepast moeten worden.

        try{ // Main return
            return localization.getString(key, locale);
        }
        catch (MissingResourceException e) {
            logger.error("Missing resource key: {}, in bundle: {}. ", key, locale, e);
        }

        try{ // Fallback return
            return localization.getString(key, localization.getFallback());
        }
        catch (MissingResourceException e) {
            logger.error("Missing resource key: {}, in default bundle!", key, e);
        }
        // Default return
        return "MISSING RESOURCE";
    }

	public static StringBinding bindToKey(String key) {
		return Bindings.createStringBinding(
			() -> localization.getString(key, locale),
			localeProperty
		);
	}
}