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

import java.util.Locale;

public final class OptionsMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
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
}