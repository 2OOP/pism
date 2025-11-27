package org.toop.app.widget.complex;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LabeledInputWidget implements Widget {
	private final TextField input;
	private final VBox container;

	public LabeledInputWidget(String key, String promptKey, String initialText, Consumer<String> onValueChanged) {
		var label = Primitive.text(key);
		input = Primitive.input(promptKey, initialText, onValueChanged);
		container = Primitive.vbox(label, input);
	}

	public String getValue() {
		return input.getText();
	}

	public void setValue(String text) {
		input.setText(text);
	}

	@Override
	public Node getNode() {
		return container;
	}
}