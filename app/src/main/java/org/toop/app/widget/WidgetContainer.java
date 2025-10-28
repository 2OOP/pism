package org.toop.app.widget;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

public final class WidgetContainer {
	private static StackPane root;

	public static StackPane setup() {
		if (root != null) {
			return root;
		}

		root = new StackPane();
		root.getStyleClass().add("bg-primary");

		return root;
	}

	public static void add(Pos position, Widget<?> widget) {
		StackPane.setAlignment(widget.getNode(), position);
		root.getChildren().add(widget.getNode());
	}

	public static void remove(Widget<?> widget) {
		root.getChildren().remove(widget.getNode());
	}
}