package org.toop.app.widget.complex;

import org.toop.app.widget.WidgetSystem;

import javafx.geometry.Pos;

public abstract class PopupWidget extends ViewWidget {
	public PopupWidget() {
		super("bg-popup");
		WidgetSystem.add(Pos.CENTER, this);
	}

	public void pop() {
		WidgetSystem.remove(this);
	}
}