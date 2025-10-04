package org.toop.local;

import org.toop.app.App;
import org.toop.framework.asset.resources.SettingsAsset;
import org.toop.framework.settings.Settings;

import java.io.File;
import java.util.Locale;

public class AppSettings {

    private SettingsAsset settingsAsset;

    public void applySettings() {
        SettingsAsset settings = getPath();
        if (!settings.isLoaded()) {
            settings.load();
        }
        Settings settingsData = settings.getContent();

        AppContext.setLocale(Locale.of(settingsData.locale));
        App.setFullscreen(settingsData.fullScreen);
    }

    public SettingsAsset getPath() {
        if (this.settingsAsset == null) {
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

            File settingsFile = new File(basePath + File.separator + "ISY1" + File.separator + "settings.json");
            this.settingsAsset = new SettingsAsset(settingsFile);
        }
        return this.settingsAsset;
    }
}
