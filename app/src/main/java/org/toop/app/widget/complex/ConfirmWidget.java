package org.toop.app.widget.complex;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ConfirmWidget implements Widget {
	private final HBox buttonsContainer;
	private final VBox container;

	public ConfirmWidget(String confirm) {
		buttonsContainer = Primitive.hbox();
		container = Primitive.vbox(Primitive.header(confirm), buttonsContainer);
	}

	public void addButton(String key, Runnable onClick) {
		Platform.runLater(() -> {
			var button = Primitive.button(key, onClick);
			buttonsContainer.getChildren().add(button);
		});
	}

	@Override
	public Node getNode() {
		return container;
	}
}