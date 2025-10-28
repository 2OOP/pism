package org.toop.app.widget.complex;

import javafx.geometry.Pos;
import org.toop.app.widget.Widget;

import javafx.scene.layout.StackPane;

public abstract class ViewWidget implements Widget<StackPane> {
    private final StackPane container;

	public ViewWidget(String cssClass) {
        container = new StackPane();
		container.getStyleClass().add(cssClass);
	}

	public void add(Pos position, Widget<?> widget) {
		StackPane.setAlignment(widget.getNode(), position);
		container.getChildren().add(widget.getNode());
	}

	@Override
	public StackPane getNode() {
		return container;
	}

	public abstract void reload();
}