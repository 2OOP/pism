package org.toop.app.layer;

import org.toop.app.events.AppEvents;
import org.toop.framework.eventbus.GlobalEventBus;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.function.Consumer;

public abstract class Container {
	public abstract Region getContainer();

	public abstract void addNode(Node node);
	public abstract void addContainer(Container container, boolean fill);

	public void addText(String cssClass, String x, boolean wrap) {
		final Text element = new Text(x);
		element.getStyleClass().add(cssClass);

		if (wrap) {
			addNode(new TextFlow(element));
		} else {
			addNode(element);
		}
	}

	public void addText(String x, boolean wrap) {
		addText("text", x, wrap);
	}

	public void addButton(String cssClass, String x, Runnable runnable) {
		final Label element = new Label(x);
		element.getStyleClass().add(cssClass);

		element.setOnMouseEntered(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeHover());
		});

		element.setOnMouseClicked(_ -> {
			runnable.run();
		});

		addNode(element);
	}

	public void addButton(String x, Runnable runnable) {
		addButton("button", x, runnable);
	}

	public void addToggle(String cssClass, String x1, String x2, boolean toggled, Consumer<Boolean> consumer) {
		final Label element = new Label(toggled ? x2 : x1);
		element.getStyleClass().add(cssClass);

		final BooleanProperty checked = new SimpleBooleanProperty(toggled);

		element.setOnMouseEntered(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeHover());
		});

		element.setOnMouseClicked(_ -> {
			checked.set(!checked.get());

			if (checked.get()) {
				element.setText(x1);
			} else {
				element.setText(x2);
			}

			consumer.accept(checked.get());
		});

		addNode(element);
	}

	public void addToggle(String x1, String x2, boolean toggled, Consumer<Boolean> consumer) {
		addToggle("toggle", x1, x2, toggled, consumer);
	}

	public void addSlider(String cssClass, int max, int initial, Consumer<Integer> consumer) {
		final Slider element = new Slider(0, max, initial);
		element.getStyleClass().add(cssClass);

		element.setMinorTickCount(0);
		element.setMajorTickUnit(1);
		element.setBlockIncrement(1);

		element.setSnapToTicks(true);
		element.setShowTickLabels(true);

		element.setOnMouseEntered(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeHover());
		});

		element.valueProperty().addListener((_, _, newValue) -> {
			consumer.accept(newValue.intValue());
		});

		addNode(element);
	}

	public void addSlider(int max, int initial, Consumer<Integer> consumer) {
		addSlider("slider", max, initial, consumer);
	}

	public void addInput(String cssClass, String input, Consumer<String> consumer) {
		final TextField element = new TextField(input);
		element.getStyleClass().add(cssClass);

		element.setOnMouseEntered(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeHover());
		});

		element.textProperty().addListener((_, _, newValue) -> {
			consumer.accept(newValue);
		});

		addNode(element);
	}

	public void addInput(String input, Consumer<String> consumer) {
		addInput("input", input, consumer);
	}

	public void addSeparator(String cssClass, boolean horizontal) {
		final Separator element = new Separator(horizontal ? Orientation.HORIZONTAL : Orientation.VERTICAL);
		element.getStyleClass().add(cssClass);
		element.setMinSize(50, 50);

		addNode(element);
	}

	public void addSeparator(boolean horizontal) {
		addSeparator("separator", horizontal);
	}
}