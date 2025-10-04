package org.toop.app.layer;

import org.toop.app.events.AppEvents;
import org.toop.framework.eventbus.GlobalEventBus;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
		} else { addNode(element); }

		return element;
	}

	public Text addText(String x, boolean wrap) {
		return addText("text", x, wrap);
	}

	public Button addButton(String cssClass, String x, Runnable runnable) {
		final Button element = new Button(x);
		element.getStyleClass().add(cssClass);

		element.setOnMouseEntered(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeHover());
		});

		element.setOnAction(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeClick());
			runnable.run();
		});

		addNode(element);
		return element;
	}

	public Button addButton(String x, Runnable runnable) {
		return addButton("button", x, runnable);
	}

	public Label addToggle(String cssClass, String x1, String x2, boolean toggled, Consumer<Boolean> consumer) {
		final Label element = new Label(toggled? x2 : x1);
		element.getStyleClass().add(cssClass);

		final BooleanProperty checked = new SimpleBooleanProperty(toggled);

		element.setOnMouseEntered(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeHover());
		});

		element.setOnMouseClicked(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeClick());
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
}