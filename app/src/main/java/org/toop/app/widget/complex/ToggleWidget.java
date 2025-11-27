package org.toop.app.widget.complex;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;
import org.toop.local.AppContext;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ToggleWidget implements Widget {
	private final Button button;
	private final VBox container;

	private final String onKey;
	private final String offKey;

	private boolean state;

	public ToggleWidget(String onKey, String offKey, boolean initialState, Consumer<Boolean> onToggle) {
		this.onKey = onKey;
		this.offKey = offKey;
		this.state = initialState;

		button = new Button(AppContext.getString(getCurrentKey()));
		button.setOnAction(_ -> {
			state = !state;
			updateText();
			if (onToggle != null) {
				onToggle.accept(state);
			}
		});

		container = Primitive.vbox(button);
	}

	private String getCurrentKey() {
		return state? offKey : onKey;
	}

	private void updateText() {
		button.setText(AppContext.getString(getCurrentKey()));
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean newState) {
		if (state != newState) {
			state = newState;
			updateText();
		}
	}

	@Override
	public Node getNode() {
		return container;
	}
}