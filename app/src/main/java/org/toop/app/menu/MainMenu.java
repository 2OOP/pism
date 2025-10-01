package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.asset.resources.CssAsset;
import org.toop.framework.asset.resources.ImageAsset;

public final class MainMenu extends Menu {
	public MainMenu() {
		final Button tictactoe = createButton("Tic Tac Toe", () -> {});
		final Button reversi = createButton("Reversi", () -> {});
		final Button sudoku = createButton("Sudoku", () -> {});
		final Button battleship = createButton("Battleship", () -> {});
		final Button other = createButton("Other", () -> {});

		final VBox gamesBox = new VBox(tictactoe, reversi, sudoku, battleship, other);
		gamesBox.setAlignment(Pos.TOP_CENTER);

		final Button credits = createButton("Credits", () -> {});
		final Button options = createButton("Options", () -> {});
		final Button quit = createButton("Quit", () -> {});

		final VBox creditsBox = new VBox(credits, options, quit);
		creditsBox.setAlignment(Pos.BOTTOM_CENTER);

		VBox grid = new VBox(20, gamesBox, creditsBox);
		grid.setAlignment(Pos.CENTER);

		ImageAsset backgroundImage = (ImageAsset) AssetManager.getByName("background.jpg").getResource();
		ImageView background = new ImageView(backgroundImage.getImage());
		background.setPreserveRatio(false);
		background.fitWidthProperty().bind(grid.widthProperty());
		background.fitHeightProperty().bind(grid.heightProperty());

		pane = new StackPane(background, grid);
		CssAsset css = (CssAsset) AssetManager.getByName("main.css").getResource();
		pane.getStylesheets().add(css.getUrl());
	}
}