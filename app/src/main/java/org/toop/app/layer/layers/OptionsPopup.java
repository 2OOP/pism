package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.Popup;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.framework.asset.resources.SettingsAsset;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;
import org.toop.local.AppSettings;

import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.util.Locale;

public final class OptionsPopup extends Popup {
    AppSettings appSettings = new AppSettings();
    SettingsAsset settings = appSettings.getPath();
	private boolean isWindowed = !(settings.getFullscreen());

	public OptionsPopup() {
		super(true, "bg-primary");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final var languageHeader = NodeBuilder.header(AppContext.getString("language"));
		final var languageSeparator = NodeBuilder.separator();

		final var volumeHeader = NodeBuilder.header(AppContext.getString("volume"));
		final var volumeSeparator = NodeBuilder.separator();

        final var fxVolumeHeader = NodeBuilder.header(AppContext.getString("effectsVolume"));
        final var fxVolumeSeparator = NodeBuilder.separator();

        final var musicVolumeHeader = NodeBuilder.header(AppContext.getString("musicVolume"));
        final var musicVolumeSeparator = NodeBuilder.separator();

		final var themeHeader = NodeBuilder.header(AppContext.getString("theme"));
		final var themeSeparator = NodeBuilder.separator();

		final var layoutSizeHeader = NodeBuilder.header(AppContext.getString("layoutSize"));
		final var layoutSizeSeparator = NodeBuilder.separator();

		final var optionsContainer = new VerticalContainer(5);
		optionsContainer.addNodes(languageHeader, languageChoiceBox(), languageSeparator);
		optionsContainer.addNodes(volumeHeader, volumeSlider(), volumeSeparator);
        optionsContainer.addNodes(fxVolumeHeader, fxVolumeSlider(), fxVolumeSeparator);
        optionsContainer.addNodes(musicVolumeHeader, musicVolumeSlider(), musicVolumeSeparator);
		optionsContainer.addNodes(themeHeader, themeChoiceBox(), themeSeparator);
		optionsContainer.addNodes(layoutSizeHeader, layoutSizeChoiceBox(), layoutSizeSeparator);
		optionsContainer.addNodes(fullscreenToggle());

		final Container mainContainer = new VerticalContainer(50, "");
		mainContainer.addContainer(optionsContainer, true);

		final var backButton = NodeBuilder.button(AppContext.getString("back"), () -> {
			App.pop();
		});

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addNodes(backButton);

		addContainer(mainContainer, Pos.CENTER, 0, 0, 0, 0);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}

	private ChoiceBox<Locale> languageChoiceBox() {
		assert AppContext.getLocalization() != null;

		final ChoiceBox<Locale> languageChoiceBox = NodeBuilder.choiceBox((locale) -> {
			if (locale == AppContext.getLocale()) {
				return;
			}

			settings.setLocale(locale.toString());
			AppContext.setLocale(locale);

			App.reloadAll();
		});

		languageChoiceBox.setConverter(new javafx.util.StringConverter<>() {
			@Override
			public String toString(Locale locale) {
				return AppContext.getString(locale.getDisplayName().toLowerCase());
			}

			@Override
			public Locale fromString(String string) {
				return null;
			}
		});

		languageChoiceBox.getItems().addAll(AppContext.getLocalization().getAvailableLocales());
		languageChoiceBox.setValue(AppContext.getLocale());

		return languageChoiceBox;
	}

	private Slider volumeSlider() {
		return NodeBuilder.slider(100, settings.getVolume(), (volume) -> {
			settings.setVolume(volume);
			new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(volume.doubleValue())).asyncPostEvent();
		});
	}

    private Slider fxVolumeSlider() {
        return NodeBuilder.slider(100, settings.getFxVolume(), (volume) -> {
            settings.setFxVolume(volume);
            new EventFlow().addPostEvent(new AudioEvents.ChangeFxVolume(volume.doubleValue())).asyncPostEvent();
        });
    }

    private Slider musicVolumeSlider() {
        return NodeBuilder.slider(100, settings.getMusicVolume(), (volume) -> {
            settings.setMusicVolume(volume);
            new EventFlow().addPostEvent(new AudioEvents.ChangeMusicVolume(volume.doubleValue())).asyncPostEvent();
        });
    }


	private Label fullscreenToggle() {
		return NodeBuilder.toggle(AppContext.getString("windowed"), AppContext.getString("fullscreen"), !isWindowed, (fullscreen) -> {
			isWindowed = !fullscreen;

            settings.setFullscreen(fullscreen);
			App.setFullscreen(fullscreen);
        });
	}

	private ChoiceBox<String> themeChoiceBox() {
		final ChoiceBox<String> themeChoiceBox = NodeBuilder.choiceBox((theme) -> {
			if (theme.equalsIgnoreCase(settings.getTheme())) {
				return;
			}

			settings.setTheme(theme);
			App.setStyle(theme, settings.getLayoutSize());
		});

		themeChoiceBox.setConverter(new javafx.util.StringConverter<>() {
			@Override
			public String toString(String theme) {
				return AppContext.getString(theme);
			}

			@Override
			public String fromString(String string) {
				return null;
			}
		});

		themeChoiceBox.getItems().addAll("dark", "light", "dark-hc", "light-hc");
		themeChoiceBox.setValue(settings.getTheme());

		return themeChoiceBox;
	}

	private ChoiceBox<String> layoutSizeChoiceBox() {
		final ChoiceBox<String> layoutSizeChoiceBox = NodeBuilder.choiceBox((layoutSize) -> {
			if (layoutSize.equalsIgnoreCase(settings.getLayoutSize())) {
				return;
			}

			settings.setLayoutSize(layoutSize);
			App.setStyle(settings.getTheme(), layoutSize);
		});

		layoutSizeChoiceBox.setConverter(new javafx.util.StringConverter<>() {
			@Override
			public String toString(String layoutSize) {
				return AppContext.getString(layoutSize);
			}

			@Override
			public String fromString(String string) {
				return null;
			}
		});

		layoutSizeChoiceBox.getItems().addAll("small", "medium", "large");
		layoutSizeChoiceBox.setValue(settings.getLayoutSize());

		return layoutSizeChoiceBox;
	}
}