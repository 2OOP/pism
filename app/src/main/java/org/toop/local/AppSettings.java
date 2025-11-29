package org.toop.local;

import org.toop.app.App;
import org.toop.framework.audio.VolumeControl;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.ResourceMeta;
import org.toop.framework.resource.resources.SettingsAsset;
import org.toop.framework.settings.Settings;

import java.io.File;
import java.util.Locale;

public class AppSettings {
	private static SettingsAsset settingsAsset;

	public static void applySettings() {
		settingsAsset = getPath();
		if (!settingsAsset.isLoaded()) {
			settingsAsset.load();
		}

        checkSettings();

		Settings settingsData = settingsAsset.getContent();

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

	public static SettingsAsset getPath() {
		if (settingsAsset == null) {
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
//            this.settingsAsset = new SettingsAsset(settingsFile);
			ResourceManager.addAsset(new ResourceMeta<>("settings.json", new SettingsAsset(settingsFile)));
		}
		return ResourceManager.get("settings.json");
	}

	public static SettingsAsset getSettings() {
		return settingsAsset;
	}

    public static void checkSettings() {
        Settings s = settingsAsset.getContent();
        boolean changed = false;

        if (s.showTutorials == null) {
            settingsAsset.setTutorialFlag(true);
            changed = true;
        }
        if (s.firstReversi == null) {
            settingsAsset.setFirstReversi(true);
            changed = true;
        }
        if (s.firstTTT == null) {
            settingsAsset.setFirstTTT(true);
            changed = true;
        }
        if (s.firstConnect4 == null) {
            settingsAsset.setFirstConnect4(true);
            changed = true;
        }
        if (changed) {
            getSettings().save();
        }
    }

    public static void doDefaultSettings() {
        settingsAsset.setFirstConnect4(true);
        settingsAsset.setFirstTTT(true);
        settingsAsset.setFirstReversi(true);
        settingsAsset.setLocale("en");
        settingsAsset.setTheme("dark");
        settingsAsset.setFullscreen(false);
        settingsAsset.setVolume(100);
        settingsAsset.setFxVolume(20);
        settingsAsset.setMusicVolume(15);
        settingsAsset.setTutorialFlag(true);
        settingsAsset.setLayoutSize("medium");
    }
}