package org.toop.app.widget.complex;

import org.toop.app.interfaces.Popup;
import org.toop.app.widget.WidgetContainer;

import javafx.geometry.Pos;

public abstract class PopupWidget extends ViewWidget implements Popup {
	public PopupWidget() {
		super("bg-popup");
	}

    public void push() {
        WidgetContainer.add(Pos.CENTER, this);
    }

	public void pop() {
		WidgetContainer.remove(this);
	}
}