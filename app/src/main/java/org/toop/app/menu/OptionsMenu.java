package org.toop.app.menu;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.LocalizationAsset;
import org.toop.framework.eventbus.EventFlow;
import org.toop.local.AppContext;
import org.toop.local.LocalizationEvents;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Locale;

public final class OptionsMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private LocalizationAsset loc = ResourceManager.get("localization");

    private final LocalizationAsset loc = ResourceManager.get("localization.properties");
    private Text chooseLang;
    private Button english,dutch,german,french,italian,spanish,chinese;

    public OptionsMenu() {
        final Region background = createBackground("quit_background");

        chooseLang   = createText(loc.getString("languageChangeText",currentLocale));
        english    = createButton(loc.getString("languageEnglish",currentLocale), () -> AppContext.setCurrentLocale(Locale.ENGLISH));
        dutch      = createButton(loc.getString("languageDutch",currentLocale), () -> AppContext.setCurrentLocale(Locale.of("nl")));
        german     = createButton(loc.getString("languageGerman",currentLocale), () -> AppContext.setCurrentLocale(Locale.of("de")));
        french     = createButton(loc.getString("languageFrench",currentLocale), () -> AppContext.setCurrentLocale(Locale.of("fr")));
        italian    = createButton(loc.getString("languageItalian",currentLocale), () -> AppContext.setCurrentLocale(Locale.of("it")));
        spanish    = createButton(loc.getString("languageSpanish",currentLocale), () -> AppContext.setCurrentLocale(Locale.of("es")));
        chinese    = createButton(loc.getString("languageChinese",currentLocale), () -> AppContext.setCurrentLocale(Locale.of("zh")));

        final VBox buttons = new VBox(10, chooseLang, english,dutch,german,french,italian,spanish,chinese);

        buttons.setAlignment(Pos.CENTER);
        buttons.getStyleClass().add("quit_box");
        buttons.setMaxWidth(300);
        buttons.setMaxHeight(600);
        pane = new StackPane(background, buttons);

        try {
            new EventFlow()
                    .listen(this::handleChangeLanguage);

        }catch (Exception e){
            System.out.println("Something went wrong while trying to change the language.");
            throw e;
        }

    }
    private void handleChangeLanguage(LocalizationEvents.LanguageHasChanged event) {
        Platform.runLater(() -> {
            currentLocale = AppContext.getLocale();
            chooseLang.setText(loc.getString("languageChangeText",currentLocale));
            english.setText(loc.getString("languageEnglish",currentLocale));
            dutch.setText(loc.getString("languageDutch",currentLocale));
            german.setText(loc.getString("languageGerman",currentLocale));
            french.setText(loc.getString("languageFrench",currentLocale));
            italian.setText(loc.getString("languageItalian",currentLocale));
            spanish.setText(loc.getString("languageSpanish",currentLocale));
            chinese.setText(loc.getString("languageChinese",currentLocale));
        });

    }
        final Region background = createBackground();

        GraphicsDevice currentScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

        LocalizationAsset locFiles = ResourceManager.get(LocalizationAsset.class, "localization");
        final Label selectLanguageLabel = new Label(loc.getString("optionsMenuLabelSelectLanguage", currentLocale));
        final ChoiceBox<Locale> selectLanguage = new ChoiceBox<>();
        selectLanguage.setValue(currentLocale);
        for (Locale locFile : locFiles.getAvailableLocales()) {
            selectLanguage.getItems().add(locFile);
        }

        selectLanguage.setOnAction((event) -> {
            Locale selectedLocale = selectLanguage.getSelectionModel().getSelectedItem();
            AppContext.setLocale(selectedLocale);
            App.pop();
            App.push(new OptionsMenu());
        });

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice[] devices = ge.getScreenDevices();
//        final ChoiceBox<GraphicsDevice> selectScreen = new ChoiceBox<>();
//        for (GraphicsDevice screen : devices) {
//            selectScreen.getItems().add(screen);
//        }
//
//        selectScreen.setOnAction((event) -> {
//            int selectedIndex = selectScreen.getSelectionModel().getSelectedIndex();
//            Object selectedItem = selectScreen.getSelectionModel().getSelectedItem();
//
//            System.out.println("Selection made: [" + selectedIndex + "] " + selectedItem);
//            System.out.println("   ChoiceBox.getValue(): " + selectScreen.getValue());
//        });
//
//        final ChoiceBox<DisplayMode> selectWindowSize = new ChoiceBox<>();
//        for (DisplayMode displayMode : currentScreen.getDisplayModes()) {
//            selectWindowSize.getItems().add(displayMode);
//        }
//
////        if (currentScreen.isFullScreenSupported()) {}
//        final CheckBox setFullscreen = new CheckBox("Fullscreen");

        final VBox optionsBox = new VBox(10, selectLanguageLabel, selectLanguage);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPickOnBounds(false);
        optionsBox.setTranslateY(50);
        optionsBox.setTranslateX(25);

        final Button credits = createButton("Credits", () -> { App.push(new CreditsMenu()); });
        final Button options = createButton("Exit Options", () -> { App.push(new MainMenu()); });
        final Button quit = createButton("Quit", () -> { App.quitPopup(); });

        final VBox controlBox = new VBox(10, credits, options, quit);
        controlBox.setAlignment(Pos.BOTTOM_LEFT);
        controlBox.setPickOnBounds(false);
        controlBox.setTranslateY(-50);
        controlBox.setTranslateX(25);

        pane = new StackPane(background, optionsBox, controlBox);
    }
}