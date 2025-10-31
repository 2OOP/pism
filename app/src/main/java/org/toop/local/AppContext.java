package org.toop.local;

import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.LocalizationAsset;

import java.util.Locale;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppContext {
    private static final LocalizationAsset localization = ResourceManager.get("localization");
    private static Locale locale = Locale.forLanguageTag("en");

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
        return localization.getString(key, locale);
    }

	public static StringBinding bindToKey(String key) {
		return Bindings.createStringBinding(
			() -> localization.getString(key, locale),
			localeProperty
		);
	}
}