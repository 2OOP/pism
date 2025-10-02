package org.toop.app.menu;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public abstract class Menu {
	protected Pane pane;
	public Pane getPane() { return pane; }

	public Region createBackground(String css) {
		final Region background = new Region();
		background.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
		background.getStyleClass().add(css);

		return background;
	}

	public Region createBackground() {
		return createBackground("background");
	}

	public Text createText(String css, String x) {
		final Text text = new Text(x);
		text.getStyleClass().add(css);

		return text;
	}

	public Text createText(String x) {
		return createText("text", x);
	}

	public Button createButton(String css, String x, Runnable runnable) {
		final Button button = new Button(x);
		button.setOnAction(_ -> runnable.run());
		button.getStyleClass().add(css);

		return button;
	}

	public Button createButton(String x, Runnable runnable) {
		return createButton("button", x, runnable);
	}
}