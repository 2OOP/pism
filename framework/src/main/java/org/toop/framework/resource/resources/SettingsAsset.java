package org.toop.framework.resource.resources;

import java.io.File;
import java.util.Locale;

import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.ResourceMeta;
import org.toop.framework.settings.Settings;

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

    public int getMusicVolume() {
        return getContent().musicVolume;
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

    public Boolean getTutorialFlag() {
        return getContent().showTutorials;
    }

    public Boolean getFirstTTT() {
        return getContent().firstTTT;
    }

    public Boolean getFirstConnect4() {
        return getContent().firstConnect4;
    }

    public Boolean getFirstReversi() {
        return getContent().firstReversi;
    }

    public boolean getSwitchReversi() {
        return getContent().switchReversi;
    }

    public void setVolume(int volume) {
        getContent().volume = volume;
        save();
    }

    public void setFxVolume(int fxVolume) {
        getContent().fxVolume = fxVolume;
        save();
    }

    public void setMusicVolume(int musicVolume) {
        getContent().musicVolume = musicVolume;
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

    public void setTutorialFlag(boolean tutorialFlag) {
        getContent().showTutorials = tutorialFlag;
        save();
    }

    public void setFirstTTT(boolean firstTTT) {
        getContent().firstTTT = firstTTT;
        save();
    }

    public void setFirstConnect4(boolean firstConnect4) {
        getContent().firstConnect4 = firstConnect4;
        save();
    }

    public void setFirstReversi(boolean firstReversi) {
        getContent().firstReversi = firstReversi;
        save();
    }

    public void setSwitchReversi(boolean switchReversi) {
        getContent().switchReversi = switchReversi;
        save();
    }

    public void setContent(Settings settings) {
        setVolume(settings.volume);
        setFxVolume(settings.fxVolume);
        setMusicVolume(settings.musicVolume);
        setLocale(settings.locale);
        setFullscreen(settings.fullScreen);
        setTheme(settings.theme);
        setLayoutSize(settings.layoutSize);
        setTutorialFlag(settings.showTutorials);
        setFirstTTT(settings.firstTTT);
        setFirstConnect4(settings.firstConnect4);
        setFirstReversi(settings.firstReversi);
        setSwitchReversi(settings.switchReversi);
    }

    public static SettingsAsset getPath() {
        SettingsAsset settingsAsset;
        try {
            settingsAsset = ResourceManager.get("settings.json");
        } catch (Exception e) {
        String os = System.getProperty("os.name").toLowerCase();
        String basePath;

        if (os.contains("win")) {
            basePath = System.getenv("APPDATA");
            if (basePath == null) {
                basePath = System.getProperty("user.home");
            }
        } else if (os.contains("mac")) {
            basePath = System.getProperty("user.home") + "/Library/Application Support";
        } else {
            basePath = System.getProperty("user.home") + "/.config";
        }

        File settingsFile =
                new File(basePath + File.separator + "ISY1" + File.separator + "settings.json");
        settingsAsset = new SettingsAsset(settingsFile);
        ResourceManager.addAsset(new ResourceMeta<>("settings.json", settingsAsset));
        }
        return settingsAsset;
    }
}
