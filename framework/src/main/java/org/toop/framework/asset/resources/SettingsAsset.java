package org.toop.framework.asset.resources;


import org.toop.framework.settings.Settings;

import java.io.File;
import java.util.Locale;

public class SettingsAsset extends JsonAsset<Settings> {

    public SettingsAsset(File file) {
        super(file, Settings.class);
    }

    public int getVolume() {
        return getContent().volume;
    }

    public int getFxVolume() {
        return getContent().fxVolume;
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(getContent().locale);
    }

    public boolean getFullscreen() {
        return getContent().fullScreen;
    }

	public String getTheme() {
		return getContent().theme;
	}

	public String getLayoutSize() {
		return getContent().layoutSize;
	}

    public void setVolume(int volume) {
        getContent().volume = volume;
        save();
    }

    public void setFxVolume(int fxVolume) {
        getContent().fxVolume = fxVolume;
        save();
    }

    public void setLocale(String locale) {
        getContent().locale = locale;
        save();
    }

    public void setFullscreen(boolean fullscreen) {
        getContent().fullScreen = fullscreen;
        save();
    }

	public void setTheme(String theme) {
		getContent().theme = theme;
		save();
	}

	public void setLayoutSize(String layoutSize) {
		getContent().layoutSize = layoutSize;
		save();
	}
}