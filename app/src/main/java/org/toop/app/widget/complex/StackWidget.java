package org.toop.app.widget.complex;

import org.toop.app.widget.Widget;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

public abstract class StackWidget extends StackPane implements Widget {
	public StackWidget(String cssClass) {
		this.getStyleClass().add(cssClass);
	}

	public void add(Pos position, Node node) {
		Platform.runLater(() -> {
			if (this.getChildren().contains(node)) {
				return;
			}

			StackPane.setAlignment(node, position);
			this.getChildren().add(node);
		});
	}

	public void add(Pos position, Widget widget) {
		add(position, widget.getNode());
	}

	public void remove(Node node) {
		Platform.runLater(() -> {
			this.getChildren().remove(node);
		});
	}

	public void remove(Widget widget) {
		remove(widget.getNode());
	}

	@Override
	public StackPane getNode() {
		return this;
	}
}