package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public final class MainMenu extends Menu {
	public MainMenu() {
		final ImageView background = new ImageView();

		final Button tictactoe = createButton("Tic Tac Toe", () -> {});
		final Button reversi = createButton("Reversi", () -> {});
		final Button sudoku = createButton("Sudoku", () -> {});
		final Button battleship = createButton("Battleship", () -> {});
		final Button other = createButton("Other", () -> {});

		final VBox gamesBox = new VBox(tictactoe, reversi, sudoku, background, other);
		gamesBox.setAlignment(Pos.TOP_CENTER);

		final Button credits = createButton("Credits", () -> {});
		final Button options = createButton("Options", () -> {});
		final Button quit = createButton("Quit", () -> {});

		final VBox creditsBox = new VBox(10, credits, options, quit);
		creditsBox.setAlignment(Pos.BOTTOM_CENTER);

		//pane = new StackPane(background, grid);
		pane.getStylesheets().add(getClass().getResource("/style/main.css").toExternalForm());
	}
}