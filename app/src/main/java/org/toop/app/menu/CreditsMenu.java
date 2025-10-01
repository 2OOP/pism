package org.toop.app.menu;

import org.toop.local.AppContext;

import java.util.Locale;
import java.util.ResourceBundle;

public final class CreditsMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("Localization", currentLocale);
    public CreditsMenu() {
	}
}