// package org.toop.app.menu;
//
// import javafx.application.Platform;
// import org.toop.app.App;
// import org.toop.app.GameType;
//
// import javafx.geometry.Pos;
// import javafx.scene.control.Button;
// import javafx.scene.layout.*;
// import org.toop.framework.asset.ResourceManager;
// import org.toop.framework.asset.resources.LocalizationAsset;
// import org.toop.framework.eventbus.EventFlow;
// import org.toop.game.tictactoe.TicTacToe;
// import org.toop.local.AppContext;
// import org.toop.local.LocalizationEvents;
//
// import java.util.Locale;
//
// public final class MainMenu extends Menu {
//     private Locale currentLocale = AppContext.getLocale();
//     private final LocalizationAsset loc = ResourceManager.get("localization");
//     private final Button tictactoe,reversi,credits,options,quit;
//     public MainMenu() {
// 		final Region background = createBackground();
//
// 		tictactoe = createButton(
//                 loc.getString("mainMenuSelectTicTacToe",currentLocale), () -> { App.activate(new GameSelectMenu(GameType.TICTACTOE)); });
// 		reversi = createButton(
//                 loc.getString("mainMenuSelectReversi",currentLocale), () -> { App.activate(new GameSelectMenu(GameType.REVERSI)); });
//
// 		final VBox gamesBox = new VBox(10, tictactoe, reversi);
// 		gamesBox.setAlignment(Pos.TOP_LEFT);
// 		gamesBox.setPickOnBounds(false);
// 		gamesBox.setTranslateY(50);
// 		gamesBox.setTranslateX(25);
//
// 		credits = createButton(loc.getString("mainMenuSelectCredits",currentLocale), () -> { App.push(new CreditsMenu()); });
// 		options = createButton(loc.getString("mainMenuSelectOptions",currentLocale), () -> { App.push(new OptionsMenu()); });
// 		quit = createButton(loc.getString("mainMenuSelectQuit",currentLocale), () -> { App.quitPopup(); });
//
// 		final VBox controlBox = new VBox(10, credits, options, quit);
// 		controlBox.setAlignment(Pos.BOTTOM_LEFT);
// 		controlBox.setPickOnBounds(false);
// 		controlBox.setTranslateY(-50);
// 		controlBox.setTranslateX(25);
//
// 		pane = new StackPane(background, gamesBox, controlBox);
//         try {
//             new EventFlow()
//                     .listen(this::handleChangeLanguage);
//
//         }catch (Exception e){
//             System.out.println("Something went wrong while trying to change the language.");
//             throw e;
//         }
//
//     }
//     private void handleChangeLanguage(LocalizationEvents.LanguageHasChanged event) {
//         Platform.runLater(() -> {
//             currentLocale = AppContext.getLocale();
//             tictactoe.setText(loc.getString("mainMenuSelectTicTacToe",currentLocale));
//             reversi.setText(loc.getString("mainMenuSelectReversi",currentLocale));
//             credits.setText(loc.getString("mainMenuSelectCredits",currentLocale));
//             options.setText(loc.getString("mainMenuSelectOptions",currentLocale));
//             quit.setText(loc.getString("mainMenuSelectQuit",currentLocale));
//         });
//
//     }
// }