package org.toop.app.widget;

import javafx.scene.image.ImageView;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.resource.resources.ImageAsset;
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
	public static Text header(String key, boolean localize) {
		var header = new Text();
		header.getStyleClass().add("header");

		if (!key.isEmpty()) {
			if (localize) header.setText(AppContext.getString(key)); else header.setText(key);
			header.textProperty().bind(AppContext.bindToKey(key, localize));
		}

		return header;
	}

	public static Text header(String key) {
		return header(key, true);
	}

	public static Text text(String key, boolean localize) {
		var text = new Text();
		text.getStyleClass().add("text");

		if (!key.isEmpty()) {
			if (localize) text.setText(AppContext.getString(key)); else text.setText(key);
			text.textProperty().bind(AppContext.bindToKey(key, localize));
		}

		return text;
	}

	public static Text text(String key) {
		return text(key, true);
	}

    public static ImageView image(ImageAsset imageAsset) {
        ImageView imageView = new ImageView(imageAsset.getImage());
        imageView.getStyleClass().add("image");
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(400);
        imageView.setFitHeight(400);
        return imageView;
    }

	public static Button button(String key, Runnable onAction, boolean localize) {
		var button = new Button();
		button.getStyleClass().add("button");

		if (!key.isEmpty()) {
			if (localize) button.setText(AppContext.getString(key));  else button.setText(key);
			button.textProperty().bind(AppContext.bindToKey(key, localize));
		}

		if (onAction != null) {
			button.setOnAction(_ -> {
                onAction.run();
                playButtonSound();
            });
		}

		return button;
	}

	public static Button button(String key, Runnable onAction) {
		return button(key, onAction, true);
	}

	public static TextField input(String promptKey, String text, Consumer<String> onValueChanged, boolean localize) {
		var input = new TextField();
		input.getStyleClass().add("input");

		if (!promptKey.isEmpty()) {
			if (localize) input.setPromptText(AppContext.getString(promptKey)); else input.setPromptText(promptKey);
			input.promptTextProperty().bind(AppContext.bindToKey(promptKey, localize));
		}

		input.setText(text);

		if (onValueChanged != null) {
			input.textProperty().addListener((_, _, newValue) ->
				onValueChanged.accept(newValue));
		}

		return input;
	}

	public static TextField input(String promptKey, String text, Consumer<String> onValueChanged) {
		return input(promptKey, text, onValueChanged, true);
	}

	public static Slider slider(int min, int max, int value, Consumer<Integer> onValueChanged) {
		var slider = new Slider();
		slider.getStyleClass().add("slider");

		slider.setMin(min);
		slider.setMax(max);
		slider.setValue(value);

        if (onValueChanged != null) {
            slider.valueProperty().addListener((_, _, newValue) -> {
                onValueChanged.accept(newValue.intValue());
            });
        }

        slider.setOnMouseReleased(event -> {
            playButtonSound();
        });

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
            choice.valueProperty().addListener((_, _, newValue) -> {
                onValueChanged.accept(newValue);
                playButtonSound();
            });
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

		for (var node : nodes) {
			if (node != null) {
				hbox.getChildren().add(node);
			}
		}

		return hbox;
	}

	public static VBox vbox(Node... nodes) {
		var vbox = new VBox();
		vbox.getStyleClass().add("container");
		vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		for (var node : nodes) {
			if (node != null) {
				vbox.getChildren().add(node);
			}
		}

		return vbox;
	}

    private static void playButtonSound() {
        new EventFlow().addPostEvent(new AudioEvents.ClickButton()).postEvent();
    }
}