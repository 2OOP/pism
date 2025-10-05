package org.toop.local;

import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;

import java.util.Locale;

public class AppContext {
	private static final LocalizationAsset localization = ResourceManager.get("localization");
	private static Locale locale = Locale.forLanguageTag("en");

	public static LocalizationAsset getLocalization() {
		return localization;
	}

	public static void setLocale(Locale locale) {
		AppContext.locale = locale;
	}

	public static Locale getLocale() {
		return locale;
	}

	public static String getString(String key) {
		assert localization != null;
		return localization.getString(key, locale);
	}
}
