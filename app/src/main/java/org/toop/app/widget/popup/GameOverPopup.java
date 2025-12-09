package org.toop.app.widget.popup;

import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;

import javafx.geometry.Pos;

public final class GameOverPopup extends PopupWidget {
	public GameOverPopup(boolean winOrTie, String winner) {
		var confirmWidget = new ConfirmWidget("game-over");

        if (winOrTie) {
            confirmWidget.setMessage(winner + " won the game!");
        }
        else{
            confirmWidget.setMessage("It was a tie!");
        }

		confirmWidget.addButton("ok", this::hide);

		add(Pos.CENTER, confirmWidget);
	}
}