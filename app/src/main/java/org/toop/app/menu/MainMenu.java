package org.toop.app.menu;

import org.toop.app.App;
import org.toop.app.GameType;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public final class MainMenu extends Menu {
	public MainMenu() {
		final Region background = createBackground();

		final Button tictactoe = createButton("Tic Tac Toe", () -> { App.activate(new GameSelectMenu(GameType.TICTACTOE)); });
		final Button reversi = createButton("Reversi", () -> { App.activate(new GameSelectMenu(GameType.REVERSI)); });

		final VBox gamesBox = new VBox(10, tictactoe, reversi);
		gamesBox.setAlignment(Pos.TOP_LEFT);
		gamesBox.setPickOnBounds(false);
		gamesBox.setTranslateY(50);
		gamesBox.setTranslateX(25);

		final Button credits = createButton("Credits", () -> { App.push(new CreditsMenu()); });
		final Button options = createButton("Options", () -> { App.push(new OptionsMenu()); });
		final Button quit = createButton("Quit", () -> { App.quitPopup(); });

		final VBox controlBox = new VBox(10, credits, options, quit);
		controlBox.setAlignment(Pos.BOTTOM_LEFT);
		controlBox.setPickOnBounds(false);
		controlBox.setTranslateY(-50);
		controlBox.setTranslateX(25);

		pane = new StackPane(background, gamesBox, controlBox);
	}
}