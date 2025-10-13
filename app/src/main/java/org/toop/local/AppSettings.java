package org.toop.local;

import java.io.File;
import java.util.Locale;
import org.toop.app.App;
import org.toop.framework.audio.VolumeControl;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.ResourceMeta;
import org.toop.framework.resource.resources.SettingsAsset;
import org.toop.framework.settings.Settings;

public class AppSettings {

    private SettingsAsset settingsAsset;

    public void applySettings() {
        this.settingsAsset = getPath();
        if (!this.settingsAsset.isLoaded()) {
            this.settingsAsset.load();
        }

        Settings settingsData = this.settingsAsset.getContent();

        AppContext.setLocale(Locale.of(settingsData.locale));
        App.setFullscreen(settingsData.fullScreen);
        new EventFlow()
                .addPostEvent(new AudioEvents.ChangeVolume(settingsData.volume, VolumeControl.MASTERVOLUME))
                .asyncPostEvent();
        new EventFlow()
                .addPostEvent(new AudioEvents.ChangeVolume(settingsData.fxVolume, VolumeControl.FX))
                .asyncPostEvent();
        new EventFlow()
                .addPostEvent(new AudioEvents.ChangeVolume(settingsData.musicVolume, VolumeControl.MUSIC))
                .asyncPostEvent();
        App.setStyle(settingsAsset.getTheme(), settingsAsset.getLayoutSize());
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

            File settingsFile =
                    new File(basePath + File.separator + "ISY1" + File.separator + "settings.json");

            return new SettingsAsset(settingsFile);
//            this.settingsAsset = new SettingsAsset(settingsFile); // TODO
//            ResourceManager.addAsset(new ResourceMeta<>("settings.json", new SettingsAsset(settingsFile))); // TODO
        }

        return this.settingsAsset;
//        return ResourceManager.get("settings.json"); // TODO
    }
}
