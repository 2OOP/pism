package org.toop.app.widget.primary;

import org.toop.app.App;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.LabeledChoiceWidget;
import org.toop.app.widget.complex.LabeledSliderWidget;
import org.toop.app.widget.complex.PrimaryWidget;
import org.toop.app.widget.complex.ToggleWidget;
import org.toop.framework.audio.VolumeControl;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;
import org.toop.local.AppSettings;

import java.util.Locale;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class OptionsPrimary extends PrimaryWidget {
	public OptionsPrimary() {
		add(Pos.CENTER, Primitive.hbox(
			generalSection(),
			volumeSection(),
			styleSection()
		));
	}

	private VBox generalSection() {
		var languageWidget = new LabeledChoiceWidget<>(
			"language",
			new StringConverter<>() {
				@Override
				public String toString(Locale locale) {
					return AppContext.getString(locale.getDisplayName().toLowerCase());
				}
				@Override
				public Locale fromString(String s) { return null; }
			},
			AppContext.getLocale(),
			newLocale -> {
				AppSettings.getSettings().setLocale(newLocale.toString());
				AppContext.setLocale(newLocale);
				reload(new OptionsPrimary());
			},
			AppContext.getLocalization().getAvailableLocales().toArray(new Locale[0])
		);

		var fullscreenToggle = new ToggleWidget(
			"fullscreen", "windowed",
			AppSettings.getSettings().getFullscreen(),
			fullscreen -> {
				AppSettings.getSettings().setFullscreen(fullscreen);
				App.setFullscreen(fullscreen);
			}
		);

		return Primitive.vbox(
			Primitive.header("general"),
			Primitive.separator(),

			languageWidget.getNode(),
			fullscreenToggle.getNode()
		);
	}

	private VBox volumeSection() {
		var masterVolumeWidget = new LabeledSliderWidget(
			"master-volume",
			0, 100,
			AppSettings.getSettings().getVolume(),
			val -> {
				AppSettings.getSettings().setVolume(val);
				new EventFlow()
					.addPostEvent(new AudioEvents.ChangeVolume(val, VolumeControl.MASTERVOLUME))
					.asyncPostEvent();
			}
		);

		var effectsVolumeWidget = new LabeledSliderWidget(
			"effects-volume",
			0, 100,
			AppSettings.getSettings().getFxVolume(),
			val -> {
				AppSettings.getSettings().setFxVolume(val);
				new EventFlow()
					.addPostEvent(new AudioEvents.ChangeVolume(val, VolumeControl.FX))
					.asyncPostEvent();
			}
		);

		var musicVolumeWidget = new LabeledSliderWidget(
			"music-volume",
			0, 100,
			AppSettings.getSettings().getMusicVolume(),
			val -> {
				AppSettings.getSettings().setMusicVolume(val);
				new EventFlow()
					.addPostEvent(new AudioEvents.ChangeVolume(val, VolumeControl.MUSIC))
					.asyncPostEvent();
			}
		);

		return Primitive.vbox(
			Primitive.header("volume"),
			Primitive.separator(),

			masterVolumeWidget.getNode(),
			effectsVolumeWidget.getNode(),
			musicVolumeWidget.getNode()
		);
	}

	private VBox styleSection() {
		var themeWidget = new LabeledChoiceWidget<>(
			"theme",
			new StringConverter<>() {
				@Override
				public String toString(String theme) {
					return AppContext.getString(theme);
				}
				@Override
				public String fromString(String s) { return null; }
			},
			AppSettings.getSettings().getTheme(),
			newTheme -> {
				AppSettings.getSettings().setTheme(newTheme);
				App.setStyle(newTheme, AppSettings.getSettings().getLayoutSize());
			},
			"dark", "light", "high-contrast"
		);

		var layoutWidget = new LabeledChoiceWidget<>(
			"layout-size",
			new StringConverter<>() {
				@Override
				public String toString(String layout) {
					return AppContext.getString(layout);
				}
				@Override
				public String fromString(String s) { return null; }
			},
			AppSettings.getSettings().getLayoutSize(),
			newLayout -> {
				AppSettings.getSettings().setLayoutSize(newLayout);
				App.setStyle(AppSettings.getSettings().getTheme(), newLayout);
			},
			"small", "medium", "large"
		);


		return Primitive.vbox(
			Primitive.header("style"),
			Primitive.separator(),

			themeWidget.getNode(),
			layoutWidget.getNode()
		);
	}
}