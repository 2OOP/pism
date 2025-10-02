package org.toop.app.menu;

import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.local.AppContext;

import java.util.Locale;

public final class CreditsMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private LocalizationAsset loc = ResourceManager.get("localization.properties");
    public CreditsMenu() {
	}
}