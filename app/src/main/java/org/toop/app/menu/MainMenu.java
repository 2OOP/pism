package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.toop.framework.asset.resources.FontAsset;
import org.toop.framework.asset.resources.LocalizationAsset;

import java.util.Locale;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.asset.resources.CssAsset;
import org.toop.framework.asset.resources.ImageAsset;
import org.toop.local.AppContext;

public final class MainMenu extends Menu {
	private final Locale currentLocale = AppContext.getLocale();
	private final LocalizationAsset loc = AssetManager.get("localization.properties");

    public MainMenu() {

		final Button tictactoe = createButton(loc.getString("mainMenuSelectTicTacToe", currentLocale), () -> {});
		final Button reversi = createButton(loc.getString("mainMenuSelectReversi", currentLocale), () -> {});
		final Button sudoku = createButton(loc.getString("mainMenuSelectSudoku", currentLocale), () -> {});
		final Button battleship = createButton(loc.getString("mainMenuSelectBattleship", currentLocale), () -> {});
		final Button other = createButton(loc.getString("mainMenuSelectOther", currentLocale), () -> {});

		final VBox gamesBox = new VBox(tictactoe, reversi, sudoku, battleship, other);
		gamesBox.setAlignment(Pos.TOP_CENTER);

		final Button credits = createButton(loc.getString("mainMenuSelectCredits", currentLocale), () -> {});
		final Button options = createButton(loc.getString("mainMenuSelectOptions", currentLocale), () -> {});
		final Button quit = createButton(loc.getString("mainMenuSelectQuit", currentLocale), () -> {});

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
		CssAsset css = AssetManager.get("main.css");
		pane.getStylesheets().add(css.getUrl());
	}
}