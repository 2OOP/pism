package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.toop.local.AppContext;

import java.util.Locale;
import java.util.ResourceBundle;

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

		final VBox gamesBox = new VBox(tictactoe, reversi, sudoku, background, other);
		gamesBox.setAlignment(Pos.TOP_CENTER);

		final Button credits = createButton(resourceBundle.getString("mainMenuSelectCredits"), () -> {});
		final Button options = createButton(resourceBundle.getString("mainMenuSelectOptions"), () -> {});
		final Button quit = createButton(resourceBundle.getString("mainMenuSelectQuit"), () -> {});

		final VBox creditsBox = new VBox(10, credits, options, quit);
		creditsBox.setAlignment(Pos.BOTTOM_CENTER);

		pane = new StackPane(background, grid);
		pane.getStylesheets().add(getClass().getResource("/style/main.css").toExternalForm());
	}
}