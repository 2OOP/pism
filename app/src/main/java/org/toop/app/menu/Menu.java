package org.toop.app.menu;

import org.toop.app.App;

import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.toop.local.AppContext;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class Menu {
	protected Pane pane;
	public Pane getPane() { return pane; }
    private Locale currentLocale = AppContext.getLocale();
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("Localization", currentLocale);

	public void fadeBackgroundImage(String imagePath, float from, float to, float milliseconds) {
		final FadeTransition fade = new FadeTransition(Duration.millis(milliseconds), App.getRoot());
		fade.setFromValue(from);
		fade.setToValue(to);
		fade.play();
	}

	public Button createButton(String text, Runnable runnable) {
		final Button button = new Button(text);
		button.setOnAction(_ -> runnable.run());
		button.getStyleClass().add("button");
		return button;
	}
}