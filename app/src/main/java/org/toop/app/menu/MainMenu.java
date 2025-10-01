package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.toop.local.AppContext;

import java.util.Locale;
import java.util.ResourceBundle;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.asset.resources.CssAsset;
import org.toop.framework.asset.resources.ImageAsset;

public final class MainMenu extends Menu {
	private Locale currentLocale = AppContext.getLocale();
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("Localization", currentLocale);

    public MainMenu() {
		final ImageView background = new ImageView();

		final Button tictactoe = createButton(resourceBundle.getString("mainMenuSelectTicTacToe"), () -> {});
		final Button reversi = createButton(resourceBundle.getString("mainMenuSelectReversi"), () -> {});
		final Button sudoku = createButton(resourceBundle.getString("mainMenuSelectSudoku"), () -> {});
		final Button battleship = createButton(resourceBundle.getString("mainMenuSelectBattleship"), () -> {});
		final Button other = createButton(resourceBundle.getString("mainMenuSelectOther"), () -> {});

		final VBox gamesBox = new VBox(tictactoe, reversi, sudoku, battleship, other);
		gamesBox.setAlignment(Pos.TOP_CENTER);

		final Button credits = createButton(resourceBundle.getString("mainMenuSelectCredits"), () -> {});
		final Button options = createButton(resourceBundle.getString("mainMenuSelectOptions"), () -> {});
		final Button quit = createButton(resourceBundle.getString("mainMenuSelectQuit"), () -> {});

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