package org.toop.app.widget.complex;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ConfirmWidget implements Widget {
	private final HBox buttonsContainer;
	private final Text messageText;
	private final VBox container;

	public ConfirmWidget(String confirm) {
		buttonsContainer = Primitive.hbox();
		messageText = Primitive.text("");
		container = Primitive.vbox(Primitive.header(confirm), messageText, Primitive.separator(), buttonsContainer);
	}

	public void setMessage(String message) {
		messageText.setText(message);
	}

	public void addButton(String key, Runnable onClick) {
		Platform.runLater(() -> {
			var button = Primitive.button(key, onClick, false);
			buttonsContainer.getChildren().add(button);
		});
	}

	@Override
	public Node getNode() {
		return container;
	}
}