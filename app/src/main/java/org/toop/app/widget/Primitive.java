package org.toop.app.widget;

import org.toop.local.AppContext;

import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public final class Primitive {
	public static Text header(String key) {
		var header = new Text();
		header.getStyleClass().add("header");

		header.setText(AppContext.getString(key));
		header.textProperty().bind(AppContext.bindToKey(key));

		return header;
	}

	public static Text text(String key) {
		var text = new Text();
		text.getStyleClass().add("text");

		text.setText(AppContext.getString(key));
		text.textProperty().bind(AppContext.bindToKey(key));

		return text;
	}

	public static Button button(String key, Runnable onAction) {
		var button = new Button();
		button.getStyleClass().add("button");

		button.setText(AppContext.getString(key));
		button.textProperty().bind(AppContext.bindToKey(key));

		if (onAction != null) {
			button.setOnAction(_ ->
				onAction.run());
		}

		return button;
	}

	public static TextField input(String promptKey, String text, Consumer<String> onValueChanged) {
		var input = new TextField();
		input.getStyleClass().add("input");

		input.setPromptText(AppContext.getString(promptKey));
		input.promptTextProperty().bind(AppContext.bindToKey(promptKey));

		input.setText(text);

		if (onValueChanged != null) {
			input.textProperty().addListener((_, _, newValue) ->
				onValueChanged.accept(newValue));
		}

		return input;
	}

	public static Slider slider(int min, int max, int value, Consumer<Integer> onValueChanged) {
		var slider = new Slider();
		slider.getStyleClass().add("slider");

		slider.setMin(min);
		slider.setMax(max);
		slider.setValue(value);

		if (onValueChanged != null) {
			slider.valueProperty().addListener((_, _, newValue) ->
				onValueChanged.accept(newValue.intValue()));
		}

		return slider;
	}

	@SafeVarargs
	public static <T> ComboBox<T> choice(StringConverter<T> converter, T value, Consumer<T> onValueChanged, T... items) {
		var choice = new ComboBox<T>();
		choice.getStyleClass().add("choice");

		if (converter != null) {
			choice.setConverter(converter);
		}

		if (value != null) {
			choice.setValue(value);
		}

		if (onValueChanged != null) {
			choice.valueProperty().addListener((_, _, newValue) ->
				onValueChanged.accept(newValue));
		}

		choice.setItems(FXCollections.observableArrayList(items));

		return choice;
	}

	public static ScrollPane scroll(Node content) {
		var scroll = new ScrollPane();
		scroll.getStyleClass().add("scroll");
		scroll.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		scroll.setFitToWidth(true);

		scroll.setContent(content);

		return scroll;
	}

	public static Separator separator() {
		var separator = new Separator();
		separator.getStyleClass().add("separator");

		return separator;
	}

	public static HBox hbox(Node... nodes) {
		var hbox = new HBox();
		hbox.getStyleClass().add("container");
		hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		hbox.getChildren().addAll(nodes);

		return hbox;
	}

	public static VBox vbox(Node... nodes) {
		var vbox = new VBox();
		vbox.getStyleClass().add("container");
		vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		vbox.getChildren().addAll(nodes);

		return vbox;
	}
}