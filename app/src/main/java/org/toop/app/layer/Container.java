package org.toop.app.layer;

import org.toop.app.events.AppEvents;
import org.toop.framework.eventbus.GlobalEventBus;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public final class Container {
	public enum Type {
		VERTICAL, HORIZONTAL,
	}

	public static Container create(String cssClass, Type type, int spacing) {
		final Container container = new Container();

		switch (type) {
			case VERTICAL:
				container.container = new VBox(spacing);
				break;
			case HORIZONTAL:
				container.container = new HBox(spacing);
				break;
		}

		container.container.getStyleClass().add(cssClass);
		return container;
	}

	public static Container create(Type type, int spacing) {
		return create("container", type, spacing);
	}

	private Pane container;

	public Container addContainer(String cssClass, Type type, int spacing) {
		final Container element = create(cssClass, type, spacing);
		element.container.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		container.getChildren().add(element.container);
		return element;
	}

	public Container addContainer(Type type, int spacing) {
		return addContainer("container", type, spacing);
	}

	public Text addText(String cssClass, String x) {
		final Text element = new Text(x);
		element.getStyleClass().add(cssClass);

		container.getChildren().addLast(element);
		return element;
	}

	public Text addText(String x) {
		return addText("text", x);
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

		container.getChildren().addLast(element);
		return element;
	}

	public Button addButton(String x, Runnable runnable) {
		return addButton("button", x, runnable);
	}

	public Label addToggle(String cssClass, String unChecked, String checked, Consumer<Boolean> consumer) {
		final Label element = new Label(unChecked);
		element.getStyleClass().add(cssClass);

		final BooleanProperty selected = new SimpleBooleanProperty(false);

		element.setOnMouseEntered(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeHover());
		});

		element.setOnMouseClicked(_ -> {
			GlobalEventBus.post(new AppEvents.OnNodeClick());
			selected.set(!selected.get());

			if (selected.get()) {
				element.setText(checked);
			} else {
				element.setText(unChecked);
			}

			consumer.accept(selected.get());
		});

		container.getChildren().addLast(element);
		return element;
	}

	public Label addToggle(String unChecked, String checked, Consumer<Boolean> consumer) {
		return addToggle("toggle", unChecked, checked, consumer);
	}

	public Pane getContainer() { return container; }
}