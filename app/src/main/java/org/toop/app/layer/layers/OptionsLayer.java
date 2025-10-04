package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;

import java.util.Locale;

public final class OptionsLayer extends Layer {
	private Locale currentLocale = AppContext.getLocale();
	private LocalizationAsset locale = ResourceManager.get("localization");

	private static int currentVolume = 25;
	private static boolean isWindowed = true;

	OptionsLayer() {
		super("options.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container optionsContainer = new VerticalContainer(5);
		optionsContainer.addText("Language", false);
		addLanguageBox(optionsContainer);
		optionsContainer.addSeparator(true);
		optionsContainer.addText("Volume", false);
		addVolumeSlider(optionsContainer);
		optionsContainer.addSeparator(true);
		addFullscreenToggle(optionsContainer);

		final Container mainContainer = new VerticalContainer(50);
		mainContainer.addText("Options", false);
		mainContainer.addContainer(optionsContainer, true);

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addButton("Back", () -> {
			App.activate(new MainLayer());
		});

		addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 60);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}

	private void addLanguageBox(Container container) {
		final ChoiceBox<Locale> languageBox = container.addChoiceBox((locale) -> {
			if (locale == currentLocale) {
				return;
			}

			AppContext.setLocale(locale);

			this.currentLocale = AppContext.getLocale();
			this.locale = ResourceManager.get("localization");

			App.reloadAll();
		});

		for (final Locale localeFile : locale.getAvailableLocales()) {
			languageBox.getItems().add(localeFile);
		}

		languageBox.setValue(currentLocale);
	}

	private void addVolumeSlider(Container container) {
		container.addSlider(100, currentVolume, (volume) -> {
			currentVolume = volume;
			new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(volume.doubleValue() / 100.0)).asyncPostEvent();
		});
	}

	private void addFullscreenToggle(Container container) {
		container.addToggle("Windowed", "Fullscreen", !isWindowed, (fullscreen) -> {
			isWindowed = !fullscreen;
			App.setFullscreen(fullscreen);
		});
	}
}