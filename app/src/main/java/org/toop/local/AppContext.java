package org.toop.local;

import java.util.Locale;

public class AppContext {
    private static Locale currentLocale = Locale.getDefault();

    public static void setLocale(Locale locale) {
        currentLocale = locale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocale = locale;
    }

    public static Locale getLocale() {
        return currentLocale;
    }
}
