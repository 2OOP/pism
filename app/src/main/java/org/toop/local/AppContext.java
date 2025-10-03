package org.toop.local;

import org.toop.app.events.AppEvents;
import org.toop.framework.eventbus.EventFlow;

import java.util.Locale;

public class AppContext {
    private static Locale currentLocale = Locale.getDefault();

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        new EventFlow().addPostEvent(new AppEvents.OnLanguageChange(locale.getLanguage())).asyncPostEvent();
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
        new EventFlow().addPostEvent(new AppEvents.OnLanguageChange(locale.getLanguage())).asyncPostEvent();
    }

    public static Locale getLocale() {
        return currentLocale;
    }
}
