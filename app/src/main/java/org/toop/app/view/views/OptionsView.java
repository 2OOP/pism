package org.toop.app.view.views;

import org.toop.app.App;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.framework.audio.VolumeControl;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;
import org.toop.local.AppSettings;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

import java.util.Locale;

public final class OptionsView extends View {
	public OptionsView() {
		super(false, "bg-secondary");
	}

	@Override
	public void setup() {
		final Text generalHeader = header();
		generalHeader.setText(AppContext.getString("general"));

		final Text volumeHeader = header();
		volumeHeader.setText(AppContext.getString("volume"));

		final Text styleHeader = header();
		styleHeader.setText(AppContext.getString("style"));

		add(Pos.CENTER,
			fit(hboxFill(
				vboxFill(
					generalHeader,
					separator(),

					vboxFill(
						text("language-text"),
						combobox("language-combobox")
					),

					vboxFill(
						button("fullscreen-button")
					)
				),

				vboxFill(
					volumeHeader,
					separator(),

					vboxFill(
						text("master-volume-text"),
						slider("master-volume-slider")
					),

					vboxFill(
						text("effects-volume-text"),
						slider("effects-volume-slider")
					),

					vboxFill(
						text("music-volume-text"),
						slider("music-volume-slider")
					)
				),

				vboxFill(
					styleHeader,
					separator(),

					vboxFill(
						text("theme-text"),
						combobox("theme-combobox")
					),

					vboxFill(
						text("layout-text"),
						combobox("layout-combobox")
					)
				)
			))
		);

		setupLanguageOption();
		setupMasterVolumeOption();
		setupEffectsVolumeOption();
		setupMusicVolumeOption();
		setupThemeOption();
		setupLayoutOption();
		setupFullscreenOption();

		final Button backButton = button();
		backButton.setText(AppContext.getString("back"));
		backButton.setOnAction(_ -> { ViewStack.pop(); });

		add(Pos.BOTTOM_LEFT,
			vboxFill(
				backButton
			)
		);
	}

	private void setupLanguageOption() {
		final Text languageText = get("language-text");
		languageText.setText(AppContext.getString("language"));

		final ComboBox<Locale> languageCombobox = get("language-combobox");
		languageCombobox.getItems().addAll(AppContext.getLocalization().getAvailableLocales());
		languageCombobox.setValue(AppContext.getLocale());

		languageCombobox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
			AppSettings.getSettings().setLocale(newValue.toString());
			AppContext.setLocale(newValue);
			App.reload();
		});

		languageCombobox.setConverter(new StringConverter<>() {
			@Override
			public String toString(Locale locale) {
				return AppContext.getString(locale.getDisplayName().toLowerCase());
			}

			@Override
			public Locale fromString(String s) {
				return null;
			}
		});
	}

	private void setupMasterVolumeOption() {
		final Text masterVolumeText = get("master-volume-text");
		masterVolumeText.setText(AppContext.getString("master-volume"));

		final Slider masterVolumeSlider = get("master-volume-slider");
		masterVolumeSlider.setMin(0);
		masterVolumeSlider.setMax(100);
		masterVolumeSlider.setValue(AppSettings.getSettings().getVolume());

		masterVolumeSlider.valueProperty().addListener((_, _, newValue) -> {
			AppSettings.getSettings().setVolume(newValue.intValue());
			new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(newValue.doubleValue(), VolumeControl.MASTERVOLUME)).asyncPostEvent();
		});
	}

	private void setupEffectsVolumeOption() {
		final Text effectsVolumeText = get("effects-volume-text");
		effectsVolumeText.setText(AppContext.getString("effects-volume"));

		final Slider effectsVolumeSlider = get("effects-volume-slider");
		effectsVolumeSlider.setMin(0);
		effectsVolumeSlider.setMax(100);
		effectsVolumeSlider.setValue(AppSettings.getSettings().getFxVolume());

		effectsVolumeSlider.valueProperty().addListener((_, _, newValue) -> {
			AppSettings.getSettings().setFxVolume(newValue.intValue());
			new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(newValue.doubleValue(), VolumeControl.FX)).asyncPostEvent();
		});
	}

	private void setupMusicVolumeOption() {
		final Text musicVolumeText = get("music-volume-text");
		musicVolumeText.setText(AppContext.getString("music-volume"));

		final Slider musicVolumeSlider = get("music-volume-slider");
		musicVolumeSlider.setMin(0);
		musicVolumeSlider.setMax(100);
		musicVolumeSlider.setValue(AppSettings.getSettings().getMusicVolume());

		musicVolumeSlider.valueProperty().addListener((_, _, newValue) -> {
			AppSettings.getSettings().setMusicVolume(newValue.intValue());
			new EventFlow().addPostEvent(new AudioEvents.ChangeVolume(newValue.doubleValue(), VolumeControl.MUSIC)).asyncPostEvent();
		});
	}

	private void setupThemeOption() {
		final Text themeText = get("theme-text");
		themeText.setText(AppContext.getString("theme"));

		final ComboBox<String> themeCombobox = get("theme-combobox");
		themeCombobox.getItems().addAll("dark", "light", "high-contrast");
		themeCombobox.setValue(AppSettings.getSettings().getTheme());

		themeCombobox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
			AppSettings.getSettings().setTheme(newValue);
			App.setStyle(newValue, AppSettings.getSettings().getLayoutSize());
		});

		themeCombobox.setConverter(new StringConverter<>() {
			@Override
			public String toString(String theme) {
				return AppContext.getString(theme);
			}

			@Override
			public String fromString(String s) {
				return null;
			}
		});
	}

	private void setupLayoutOption() {
		final Text layoutText = get("layout-text");
		layoutText.setText(AppContext.getString("layout-size"));

		final ComboBox<String> layoutCombobox = get("layout-combobox");
		layoutCombobox.getItems().addAll("small", "medium", "large");
		layoutCombobox.setValue(AppSettings.getSettings().getLayoutSize());

		layoutCombobox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
			AppSettings.getSettings().setLayoutSize(newValue);
			App.setStyle(AppSettings.getSettings().getTheme(), newValue);
		});

		layoutCombobox.setConverter(new StringConverter<>() {
			@Override
			public String toString(String layout) {
				return AppContext.getString(layout);
			}

			@Override
			public String fromString(String s) {
				return null;
			}
		});
	}

	private void setupFullscreenOption() {
		final Button fullscreenButton = get("fullscreen-button");

		if (AppSettings.getSettings().getFullscreen()) {
			fullscreenButton.setText(AppContext.getString("windowed"));
			fullscreenButton.setOnAction(_ -> {
				AppSettings.getSettings().setFullscreen(false);
				App.setFullscreen(false);
			});
		} else {
			fullscreenButton.setText(AppContext.getString("fullscreen"));
			fullscreenButton.setOnAction(_ -> {
				AppSettings.getSettings().setFullscreen(true);
				App.setFullscreen(true);
			});
		}
	}
}