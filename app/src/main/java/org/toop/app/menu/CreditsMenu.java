// package org.toop.app.menu;
//
// import javafx.animation.Interpolator;
// import javafx.animation.PauseTransition;
// import javafx.animation.TranslateTransition;
// import javafx.application.Platform;
// import javafx.geometry.Pos;
// import javafx.scene.control.Button;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.util.Duration;
// import org.toop.app.App;
// import org.toop.framework.asset.ResourceManager;
// import org.toop.framework.asset.resources.LocalizationAsset;
// import org.toop.framework.eventbus.EventFlow;
// import org.toop.local.AppContext;
// import org.toop.local.LocalizationEvents;
//
// import java.util.Locale;
//
// public final class CreditsMenu extends Menu { ;
//     private Locale currentLocale = AppContext.getLocale();
//     private LocalizationAsset loc = ResourceManager.get("localization_en_us.properties");
//
//     String[] credits = {
//             "Scrum Master: Stef",
//             "Product Owner: Omar",
//             "Merge Commander: Bas",
//             "Localization: Ticho",
//             "AI: Michiel",
//             "Developers: Michiel, Bas, Stef, Omar, Ticho",
//             "Moral Support: Wesley (voor 1 week)",
//             "OpenGL: Omar"
//     };
//
//     double scrollDuration = 20.0;
//     double lineHeight = 40.0;
//
//     public CreditsMenu() {
//         VBox creditsBox = new VBox(lineHeight / 2);
//         for (int i = credits.length - 1; i >= 0; i--) {
//             creditsBox.getChildren().add(createText(credits[i]));
//             creditsBox.setAlignment(Pos.CENTER);
//         }
//
//         Button exit = new Button("<");
//         exit.setStyle(
//                 "-fx-background-color: transparent;" +
//                         "-fx-text-fill: white;" +
//                         "-fx-font-size: 72px;" +
//                         "-fx-padding: 10 20 10 20;"
//         );
//         exit.setOnAction(e -> App.pop());
//
//         final Region background = createBackground();
//         StackPane.setAlignment(exit, Pos.TOP_LEFT);
//         pane = new StackPane(background, creditsBox, exit);
//
//         Platform.runLater(() -> playCredits(creditsBox, 800));
//
//         try {
//             new EventFlow()
//                     .listen(this::handleChangeLanguage);
//
//         }catch (Exception e){
//             System.out.println("Something went wrong while trying to change the language.");
//             throw e;
//         }
//     }
//
//     public void playCredits(VBox creditsBox, double sceneLength) {
//         double height = (credits.length * lineHeight);
//         double startY = -sceneLength;
//         double endY = height;
//
//         creditsBox.setTranslateY(startY);
//
//         TranslateTransition scrollCredits = new TranslateTransition();
//         scrollCredits.setNode(creditsBox);
//         scrollCredits.setFromY(startY);
//         scrollCredits.setToY(endY / 2 - 200);
//         scrollCredits.setDuration(Duration.seconds(scrollDuration));
//         scrollCredits.setInterpolator(Interpolator.LINEAR);
//
//         scrollCredits.setOnFinished(e -> {
//             PauseTransition pauseCredits = new PauseTransition(Duration.seconds(5));
//             pauseCredits.setOnFinished(a -> playCredits(creditsBox, sceneLength));
//             pauseCredits.play();
//         });
//
//         scrollCredits.play();
//     }
//     private void handleChangeLanguage(LocalizationEvents.LanguageHasChanged event) {
//         Platform.runLater(() -> {
//             currentLocale = AppContext.getLocale();
//             //credits.setText(loc.getString("credits",currentLocale));
//         });
//     }
// }