package org.toop.app.widget.simple;

import org.toop.app.widget.Widget;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LabeledButtonWidget extends VBox implements Widget<VBox> {
	public LabeledButtonWidget(
		String labelText,
		String buttonText, Runnable buttonOnAction
	) {
		var text = new Text(labelText);

		var button = new Button(buttonText);
		button.setOnAction(_ -> buttonOnAction.run());

		super(text, button);
	}

	@Override
	public VBox getNode() {
		return this;
	}
}