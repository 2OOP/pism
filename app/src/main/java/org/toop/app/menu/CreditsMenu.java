package org.toop.app.menu;

import org.toop.framework.asset.AssetManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.local.AppContext;

import java.util.Locale;
import java.util.ResourceBundle;

public final class CreditsMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private LocalizationAsset loc = AssetManager.get("localization.properties");
    public CreditsMenu() {
	}
}