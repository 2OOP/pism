package org.toop.app.widget.primary;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.PrimaryWidget;

import javafx.geometry.Pos;

public class LocalPrimary extends PrimaryWidget {
	public LocalPrimary() {
		var ticTacToeButton = Primitive.button("tic-tac-toe", () -> {
		});

		var reversiButton = Primitive.button("reversi", () -> {
		});

		var connect4Button = Primitive.button("connect4", () -> {
		});

		add(Pos.CENTER, Primitive.vbox(
			ticTacToeButton,
			reversiButton,
			connect4Button
		));
	}
}