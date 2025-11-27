package org.toop.app.widget.popup;

import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.local.AppContext;

import javafx.geometry.Pos;

public final class GameOverPopup extends PopupWidget {
	public GameOverPopup(boolean iWon, String winner) {
		var confirmWidget = new ConfirmWidget("game-over");

		if (winner.isEmpty()) {
			confirmWidget.setMessage(AppContext.getString("the-game-ended-in-a-draw"));
		} else if (iWon) {
			confirmWidget.setMessage(AppContext.getString("you-win"));
		} else {
			confirmWidget.setMessage(AppContext.getString("you-lost-against") + ": " + winner);
		}

		confirmWidget.addButton("ok", () -> hide());

		add(Pos.CENTER, confirmWidget);
	}
}