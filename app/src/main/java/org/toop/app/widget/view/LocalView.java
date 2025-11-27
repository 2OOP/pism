package org.toop.app.widget.view;

import org.toop.app.GameInformation;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;

import javafx.geometry.Pos;

public class LocalView extends ViewWidget {
	public LocalView() {
		var ticTacToeButton = Primitive.button("tic-tac-toe", () -> {
			transitionNext(new LocalMultiplayerView(GameInformation.Type.TICTACTOE));
		});

		var reversiButton = Primitive.button("reversi", () -> {
			transitionNext(new LocalMultiplayerView(GameInformation.Type.REVERSI));
		});

		var connect4Button = Primitive.button("connect4", () -> {
			transitionNext(new LocalMultiplayerView(GameInformation.Type.CONNECT4));
		});

		add(Pos.CENTER, Primitive.vbox(
			ticTacToeButton,
			reversiButton,
			connect4Button
		));
	}
}