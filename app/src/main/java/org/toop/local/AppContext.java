package org.toop.local;

import org.toop.framework.eventbus.EventFlow;

import java.util.Locale;

public class AppContext {
    private static Locale currentLocale = Locale.getDefault();

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
        new EventFlow().addPostEvent(new LocalizationEvents.LanguageHasChanged(locale.getLanguage())).asyncPostEvent();
    }
    public static Locale getLocale() {
        return currentLocale;
    }
}
