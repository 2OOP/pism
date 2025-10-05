package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.framework.asset.resources.SettingsAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import org.toop.local.AppSettings;

import java.util.Locale;

public final class OptionsLayer extends Layer {
    AppSettings appSettings = new AppSettings();
    SettingsAsset settings = appSettings.getPath();

	private int currentVolume = settings.getVolume();
	private boolean isWindowed = !(settings.getFullscreen());

	OptionsLayer() {
		super("options.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container optionsContainer = new VerticalContainer(5);
		optionsContainer.addText(AppContext.getString("language"), false);
		addLanguageBox(optionsContainer);
		optionsContainer.addSeparator(true);

		optionsContainer.addText(AppContext.getString("volume"), false);
		addVolumeSlider(optionsContainer);
		optionsContainer.addSeparator(true);

		addFullscreenToggle(optionsContainer);

		final Container mainContainer = new VerticalContainer(50);
		mainContainer.addText(AppContext.getString("options"), false);
		mainContainer.addContainer(optionsContainer, true);

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addButton(AppContext.getString("back"), () -> {
			App.activate(new MainLayer());
		});

		addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 60);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}

	private void addLanguageBox(Container container) {
		assert AppContext.getLocalization() != null;

		final ChoiceBox<Locale> languageBox = container.addChoiceBox((locale) -> {
			if (locale == AppContext.getLocale()) {
				return;
			}

			AppContext.setLocale(locale);
			App.reloadAll();
		});

		for (final Locale localeFile : AppContext.getLocalization().getAvailableLocales()) {
			languageBox.getItems().add(localeFile);
		}

		languageBox.setConverter(new javafx.util.StringConverter<>() {
			@Override
			public String toString(Locale locale) {
				return AppContext.getString(locale.getDisplayName().toLowerCase());
			}

			@Override
			public Locale fromString(String string) {
				return null;
			}
		});

		languageBox.setValue(AppContext.getLocale());
	}

	private void addVolumeSlider(Container container) {
		container.addSlider(100, currentVolume, (volume) -> {
			currentVolume = volume;
            settings.setVolume(volume);
            new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(volume.doubleValue())).asyncPostEvent();
		});
	}

	private void addFullscreenToggle(Container container) {
		container.addToggle(AppContext.getString("windowed"), AppContext.getString("fullscreen"), !isWindowed, (fullscreen) -> {
			isWindowed = !fullscreen;
			App.setFullscreen(fullscreen);
            settings.setFullscreen(fullscreen);
        });
	}
}