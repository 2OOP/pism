package org.toop.app.widget.popup;

import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;

import javafx.geometry.Pos;

public class ErrorPopup extends PopupWidget {
	public ErrorPopup(String error) {
		var confirmWidget = new ConfirmWidget("error");
		confirmWidget.setMessage(error);
		confirmWidget.addButton("ok", this::hide);

		add(Pos.CENTER, confirmWidget);
	}
}