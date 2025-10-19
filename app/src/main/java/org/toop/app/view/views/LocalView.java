package org.toop.app.view.views;

import org.toop.app.GameInformation;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

public final class LocalView extends View {
	public LocalView() {
		super(true, "bg-primary");
	}

	@Override
	public void setup() {
		final Button ticTacToeButton = button();
		ticTacToeButton.setText(AppContext.getString("tic-tac-toe"));
		ticTacToeButton.setOnAction(_ -> { ViewStack.push(new LocalMultiplayerView(GameInformation.Type.TICTACTOE)); });

		final Button reversiButton = button();
		reversiButton.setText(AppContext.getString("reversi"));
		reversiButton.setOnAction(_ -> { ViewStack.push(new LocalMultiplayerView(GameInformation.Type.REVERSI)); });

        final Button connect4Button = button();
        connect4Button.setText(AppContext.getString("connect4"));
        connect4Button.setOnAction(_ -> { ViewStack.push(new LocalMultiplayerView(GameInformation.Type.CONNECT4)); });

        add(Pos.CENTER,
			fit(vboxFill(
				ticTacToeButton,
				reversiButton,
                connect4Button
			))
		);

		final Button backButton = button();
		backButton.setText(AppContext.getString("back"));
		backButton.setOnAction(_ -> { ViewStack.push(new MainView()); });

		add(Pos.BOTTOM_LEFT,
			vboxFill(
				backButton
			)
		);
	}
}