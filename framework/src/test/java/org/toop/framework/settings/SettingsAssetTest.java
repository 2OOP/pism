package org.toop.framework.settings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.framework.resource.resources.SettingsAsset;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsAssetTest {

    private Settings copySettings;
    private SettingsAsset settingsAsset;

    @BeforeEach
    void setup() {
       this.settingsAsset = SettingsAsset.getPath();
        Settings original = settingsAsset.getContent();
        this.copySettings = new Settings();
        this.copySettings.volume = original.volume;
        this.copySettings.fxVolume = original.fxVolume;
        this.copySettings.musicVolume = original.musicVolume;
        this.copySettings.locale = original.locale;
        this.copySettings.fullScreen = original.fullScreen;
        this.copySettings.theme = original.theme;
        this.copySettings.layoutSize = original.layoutSize;
        this.copySettings.showTutorials = original.showTutorials;
        this.copySettings.firstTTT = original.firstTTT;
        this.copySettings.firstConnect4 = original.firstConnect4;
        this.copySettings.firstReversi = original.firstReversi;
    }


    @AfterEach
    void teardown() {
        settingsAsset.setContent(copySettings);
    }

    @Test
    void testVolume() {
        settingsAsset.setVolume(55);
        assertEquals(55, settingsAsset.getVolume());
    }

    @Test
    void testFxVolume() {
        settingsAsset.setFxVolume(33);
        assertEquals(33, settingsAsset.getFxVolume());
    }

    @Test
    void testMusicVolume() {
        settingsAsset.setMusicVolume(77);
        assertEquals(77, settingsAsset.getMusicVolume());
    }

    @Test
    void testLocale() {
        settingsAsset.setLocale("es-ES");
        assertEquals(Locale.forLanguageTag("es-ES"), settingsAsset.getLocale());
    }

    @Test
    void testFullscreen() {
        settingsAsset.setFullscreen(true);
        assertTrue(settingsAsset.getFullscreen());
    }

    @Test
    void testTheme() {
        settingsAsset.setTheme("dark");
        assertEquals("dark", settingsAsset.getTheme());
    }

    @Test
    void testLayoutsize() {
        settingsAsset.setLayoutSize("large");
        assertEquals("large", settingsAsset.getLayoutSize());
    }

    @Test
    void testTutorialflag() {
        settingsAsset.setTutorialFlag(false);
        assertFalse(settingsAsset.getTutorialFlag());
    }

    @Test
    void testTTT() {
        settingsAsset.setFirstTTT(true);
        assertTrue(settingsAsset.getFirstTTT());
    }

    @Test
    void testConnect4() {
        settingsAsset.setFirstConnect4(false);
        assertFalse(settingsAsset.getFirstConnect4());
    }

    @Test
    void testReversi() {
        settingsAsset.setFirstReversi(true);
        assertTrue(settingsAsset.getFirstReversi());
    }

    @Test
    void testReplaceFields() {
        Settings newSettings = new Settings();
        newSettings.volume = 10;
        newSettings.fxVolume = 20;
        newSettings.musicVolume = 30;
        newSettings.locale = "fr-FR";
        newSettings.fullScreen = true;
        newSettings.theme = "light";
        newSettings.layoutSize = "medium";
        newSettings.showTutorials = false;
        newSettings.firstTTT = false;
        newSettings.firstConnect4 = true;
        newSettings.firstReversi = false;
        settingsAsset.setContent(newSettings);
        assertEquals(10, settingsAsset.getVolume());
        assertEquals(20, settingsAsset.getFxVolume());
        assertEquals(30, settingsAsset.getMusicVolume());
        assertEquals(Locale.forLanguageTag("fr-FR"), settingsAsset.getLocale());
        assertTrue(settingsAsset.getFullscreen());
        assertEquals("light", settingsAsset.getTheme());
        assertEquals("medium", settingsAsset.getLayoutSize());
        assertFalse(settingsAsset.getTutorialFlag());
        assertFalse(settingsAsset.getFirstTTT());
        assertTrue(settingsAsset.getFirstConnect4());
        assertFalse(settingsAsset.getFirstReversi());
    }
}

