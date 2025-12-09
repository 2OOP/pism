package org.toop.app.widget.popup;

import org.toop.app.App;
import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;

import javafx.geometry.Pos;

public class QuitPopup extends PopupWidget {
	public QuitPopup() {
		var confirmWidget = new ConfirmWidget("are-you-sure");

		confirmWidget.addButton("yes", () -> {
			App.quit();
		});

		confirmWidget.addButton("no", () -> {
			hide();
		});

		add(Pos.CENTER, confirmWidget);

		setOnPop(() -> {
			hide();
		});
	}
}