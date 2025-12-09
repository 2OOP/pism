package org.toop.app.widget.complex;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

public abstract class PopupWidget extends StackWidget {
	private final Button popButton;

	public PopupWidget() {
		super("bg-popup");

		popButton = new Button("X");
		popButton.setOnAction(_ -> hide());

		add(Pos.TOP_RIGHT, popButton);
	}

	protected void setOnPop(Runnable onPop) {
		popButton.setOnAction(_ -> onPop.run());
	}
}