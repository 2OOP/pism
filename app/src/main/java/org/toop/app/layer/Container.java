package org.toop.app.layer;

import org.toop.app.events.AppEvents;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.function.Consumer;

public abstract class Container {
	public abstract Region getContainer();

	public abstract void addNode(Node node);

	public abstract void addContainer(Container container, boolean fill);

	public Text addText(String cssClass, String x, boolean wrap) {
		final Text element = new Text(x);
		element.getStyleClass().add(cssClass);

		if (wrap) {
			addNode(new TextFlow(element));
		} else {
			addNode(element);
		}

		return element;
	}

	public Text addText(String x, boolean wrap) {
		return addText("text", x, wrap);
	}

	public Label addButton(String cssClass, String x, Runnable runnable) {
		final Label element = new Label(x);
		element.getStyleClass().add(cssClass);

		element.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
			runnable.run();
		});

		addNode(element);
		return element;
	}

	public Label addButton(String x, Runnable runnable) {
		return addButton("button", x, runnable);
	}

	public Label addToggle(String cssClass, String x1, String x2, boolean toggled, Consumer<Boolean> consumer) {
		final Label element = new Label(toggled ? x2 : x1);
		element.getStyleClass().add(cssClass);

		final BooleanProperty checked = new SimpleBooleanProperty(toggled);

		element.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
			checked.set(!checked.get());

			if (checked.get()) {
				element.setText(x1);
			} else {
				element.setText(x2);
			}

			consumer.accept(checked.get());
		});

		addNode(element);
		return element;
	}

	public Label addToggle(String x1, String x2, boolean toggled, Consumer<Boolean> consumer) {
		return addToggle("toggle", x1, x2, toggled, consumer);
	}

	public Slider addSlider(String cssClass, int max, int initial, Consumer<Integer> consumer) {
		final Slider element = new Slider(0, max, initial);
		element.getStyleClass().add(cssClass);

		element.setMinorTickCount(0);
		element.setMajorTickUnit(1);
		element.setBlockIncrement(1);

		element.setSnapToTicks(true);
		element.setShowTickLabels(true);

		element.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
		});

		element.valueProperty().addListener((_, _, newValue) -> {
			consumer.accept(newValue.intValue());
		});

		addNode(element);
		return element;
	}

	public Slider addSlider(int max, int initial, Consumer<Integer> consumer) {
		return addSlider("slider", max, initial, consumer);
	}

	public TextField addInput(String cssClass, String input, Consumer<String> consumer) {
		final TextField element = new TextField(input);
		element.getStyleClass().add(cssClass);

		element.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
		});

		element.textProperty().addListener((_, _, newValue) -> {
			consumer.accept(newValue);
		});

		addNode(element);
		return element;
	}

	public TextField addInput(String input, Consumer<String> consumer) {
		return addInput("input", input, consumer);
	}

	public <T> ChoiceBox<T> addChoiceBox(String cssClass, Consumer<T> consumer) {
		final ChoiceBox<T> element = new ChoiceBox<>();
		element.getStyleClass().add(cssClass);

		element.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
		});

		element.valueProperty().addListener((_, _, newValue) -> {
			consumer.accept(newValue);
		});

		addNode(element);
		return element;
	}

	public <T> ChoiceBox<T> addChoiceBox(Consumer<T> consumer) {
		return addChoiceBox("choicebox", consumer);
	}

	public Separator addSeparator(String cssClass, boolean horizontal) {
		final Separator element = new Separator(horizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL);
		element.getStyleClass().add(cssClass);
		element.setMinSize(50, 50);

		addNode(element);
		return element;
	}

	public Separator addSeparator(boolean horizontal) {
		return addSeparator("separator", horizontal);
	}
}