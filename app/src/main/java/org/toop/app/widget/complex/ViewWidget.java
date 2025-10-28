package org.toop.app.widget.complex;

import javafx.geometry.Pos;
import org.toop.app.widget.Widget;

import javafx.scene.layout.StackPane;

public abstract class ViewWidget extends StackPane implements Widget<StackPane> {
	public ViewWidget(String cssClass) {
		getStyleClass().add(cssClass);
	}

	public void add(Pos position, Widget<?> widget) {
		setAlignment(widget.getNode(), position);
		getChildren().add(widget.getNode());
	}

	@Override
	public StackPane getNode() {
		return this;
	}

	public void show() {

	}

	public abstract void reload();
}