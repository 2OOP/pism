package org.toop.app.widget.complex;

import org.toop.app.widget.Widget;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public abstract class StackWidget implements Widget {
    private final StackPane container;

	public StackWidget(String cssClass) {
        container = new StackPane();
		container.getStyleClass().add(cssClass);
	}

	public void add(Pos position, Node node) {
		Platform.runLater(() -> {
			if (container.getChildren().contains(node)) {
				return;
			}

			StackPane.setAlignment(node, position);
			container.getChildren().add(node);
		});
	}

	public void add(Pos position, Widget widget) {
		add(position, widget.getNode());
	}

	public void remove(Node node) {
		Platform.runLater(() -> {
			container.getChildren().remove(node);
		});
	}

	public void remove(Widget widget) {
		remove(widget.getNode());
	}

	@Override
	public Node getNode() {
		return container;
	}
}