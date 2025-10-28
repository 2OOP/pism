package org.toop.app.widget.complex;

import org.toop.app.widget.WidgetSystem;

import javafx.geometry.Pos;

public abstract class PrimaryWidget extends ViewWidget {
	public PrimaryWidget() {
		super("bg-primary");
	}

	public void transition(PrimaryWidget primary) {
		WidgetSystem.add(Pos.CENTER, primary);
		WidgetSystem.remove(this);
	}
}