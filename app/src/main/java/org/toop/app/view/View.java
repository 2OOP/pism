package org.toop.app.view;

import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public abstract class View {
	private final boolean mainView;

	private final StackPane view;
	private final Map<String, Node> nodeMap;

	protected View(boolean mainView, String cssClass) {
		this.mainView = mainView;

		view = new StackPane();
		view.getStyleClass().add(cssClass);

		nodeMap = new HashMap<String, Node>();
	}

	public void add(Pos position, Node node) {
		assert node != null;

		StackPane.setAlignment(node, position);
		view.getChildren().add(node);
	}

	protected Region hspacer() {
		final Region hspacer = new Region();
		hspacer.getStyleClass().add("hspacer");

		return hspacer;
	}

	protected Region vspacer() {
		final Region vspacer = new Region();
		vspacer.getStyleClass().add("vspacer");

		return vspacer;
	}

	protected ScrollPane fit(String identifier, String cssClass, Node node) {
		assert node != null;

		final ScrollPane fit = new ScrollPane(node);
		fit.getStyleClass().add(cssClass);

		fit.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		fit.setFitToWidth(true);
		fit.setFitToHeight(true);

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, fit);
		}

		return fit;
	}

	protected ScrollPane fit(String identifier, Node node) {
		return fit(identifier, "fit", node);
	}

	protected ScrollPane fit(Node node) {
		return fit("", node);
	}

	protected HBox hbox(String identifier, String cssClass, Node... nodes) {
		assert !nodeMap.containsKey(identifier);

		final HBox hbox = new HBox();
		hbox.getStyleClass().add(cssClass);
		hbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		for (final Node node : nodes) {
			if (node != null) {
				hbox.getChildren().add(node);
			}
		}

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, hbox);
		}

		return hbox;
	}

	protected HBox hbox(String identifier, Node... nodes) {
		return hbox(identifier, "container", nodes);
	}

	protected HBox hbox(Node... nodes) {
		return hbox("", nodes);
	}

	protected HBox hboxFill(String identifier, String cssClass, Node... nodes) {
		final HBox hbox = hbox(identifier, cssClass, nodes);

		for (final Node node : hbox.getChildren()) {
			if (node instanceof Region) {
				((Region)node).setMaxHeight(Double.MAX_VALUE);
			}
		}

		return hbox;
	}

	protected HBox hboxFill(String identifier, Node... nodes) {
		final HBox hbox = hbox(identifier, nodes);

		for (final Node node : hbox.getChildren()) {
			if (node instanceof Region) {
				((Region)node).setMaxHeight(Double.MAX_VALUE);
			}
		}

		return hbox;
	}

	protected HBox hboxFill(Node... nodes) {
		final HBox hbox = hbox(nodes);

		for (final Node node : hbox.getChildren()) {
			if (node instanceof Region) {
				((Region)node).setMaxHeight(Double.MAX_VALUE);
			}
		}

		return hbox;
	}

	protected VBox vbox(String identifier, String cssClass, Node... nodes) {
		assert !nodeMap.containsKey(identifier);

		final VBox vbox = new VBox();
		vbox.getStyleClass().add(cssClass);
		vbox.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

		for (final Node node : nodes) {
			if (node != null) {
				vbox.getChildren().add(node);
			}
		}

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, vbox);
		}

		return vbox;
	}

	protected VBox vbox(String identifier, Node... nodes) {
		return vbox(identifier, "container", nodes);
	}

	protected VBox vbox(Node... nodes) {
		return vbox("", nodes);
	}

	protected VBox vboxFill(String identifier, String cssClass, Node... nodes) {
		final VBox vbox = vbox(identifier, cssClass, nodes);

		for (final Node node : vbox.getChildren()) {
			if (node instanceof Region) {
				((Region)node).setMaxWidth(Double.MAX_VALUE);
			}
		}

		return vbox;
	}

	protected VBox vboxFill(String identifier, Node... nodes) {
		final VBox vbox = vbox(identifier, nodes);

		for (final Node node : vbox.getChildren()) {
			if (node instanceof Region) {
				((Region)node).setMaxWidth(Double.MAX_VALUE);
			}
		}

		return vbox;
	}

	protected VBox vboxFill(Node... nodes) {
		final VBox vbox = vbox(nodes);

		for (final Node node : vbox.getChildren()) {
			if (node instanceof Region) {
				((Region)node).setMaxWidth(Double.MAX_VALUE);
			}
		}

		return vbox;
	}

	protected Separator separator(String identifier, String cssClass) {
		assert !nodeMap.containsKey(identifier);

		final Separator separator = new Separator();
		separator.getStyleClass().add(cssClass);

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, separator);
		}

		return separator;
	}

	protected Separator separator(String identifier) {
		return separator(identifier, "separator");
	}

	protected Separator separator() {
		return separator("");
	}

	protected Text header(String identifier, String cssClass) {
		assert !nodeMap.containsKey(identifier);

		final Text header = new Text();
		header.getStyleClass().add(cssClass);

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, header);
		}

		return header;
	}

	protected Text header(String identifier) {
		return header(identifier, "header");
	}

	protected Text header() {
		return header("");
	}

	protected Text text(String identifier, String cssClass) {
		assert !nodeMap.containsKey(identifier);

		final Text text = new Text();
		text.getStyleClass().add(cssClass);

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, text);
		}

		return text;
	}

	protected Text text(String identifier) {
		return text(identifier, "text");
	}

	protected Text text() {
		return text("");
	}

	protected Button button(String identifier, String cssClass) {
		assert !nodeMap.containsKey(identifier);

		final Button button = new Button();
		button.getStyleClass().add(cssClass);

		button.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
		});

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, button);
		}

		return button;
	}

	protected Button button(String identifier) {
		return button(identifier, "button");
	}

	protected Button button() {
		return button("");
	}

	protected Slider slider(String identifier, String cssClass) {
		assert !nodeMap.containsKey(identifier);

		final Slider slider = new Slider();
		slider.getStyleClass().add(cssClass);

		slider.setMinorTickCount(0);
		slider.setMajorTickUnit(1);
		slider.setBlockIncrement(1);

		slider.setSnapToTicks(true);
		slider.setShowTickLabels(true);

		slider.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
		});

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, slider);
		}

		return slider;
	}

	protected Slider slider(String identifier) {
		return slider(identifier, "slider");
	}

	protected Slider slider() {
		return slider("");
	}

	protected TextField input(String identifier, String cssClass) {
		assert !nodeMap.containsKey(identifier);

		final TextField input = new TextField();
		input.getStyleClass().add(cssClass);

		input.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
		});

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, input);
		}

		return input;
	}

	protected TextField input(String identifier) {
		return input(identifier, "input");
	}

	protected TextField input() {
		return input("");
	}

	protected <T> ComboBox<T> combobox(String identifier, String cssClass) {
		assert !nodeMap.containsKey(identifier);

		final ComboBox<T> combobox = new ComboBox<T>();
		combobox.getStyleClass().add(cssClass);

		combobox.setOnMouseClicked(_ -> {
			new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
		});

		if (!identifier.isEmpty()) {
			nodeMap.put(identifier, combobox);
		}

		return combobox;
	}

	protected <T> ComboBox<T> combobox(String identifier) {
		return combobox(identifier, "combo-box");
	}

	protected <T> ComboBox<T> combobox() {
		return combobox("");
	}

	@SuppressWarnings("unchecked")
	protected <T extends Node> T get(String identifier) {
		assert nodeMap.containsKey(identifier);
		return (T) nodeMap.get(identifier);
	}

	protected void clear() {
		view.getChildren().clear();
		nodeMap.clear();
	}

	public boolean isMainView() { return mainView; }
	public Region getView() { return view; }

	public abstract void setup();
	public void cleanup() {
		clear();
	}
}