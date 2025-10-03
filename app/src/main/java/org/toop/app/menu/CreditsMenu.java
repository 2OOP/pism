// package org.toop.app.menu;
//
// import javafx.application.Platform;
// import org.toop.framework.asset.ResourceManager;
// import org.toop.framework.asset.resources.LocalizationAsset;
// import org.toop.framework.eventbus.EventFlow;
// import org.toop.local.AppContext;
//
// import java.util.Locale;
//
// public final class CreditsMenu extends Menu {
//     private Locale currentLocale = AppContext.getLocale();
//     private LocalizationAsset loc = ResourceManager.get("localization_en_us.properties");
//     public CreditsMenu() {
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
//             //credits.setText(loc.getString("credits",currentLocale));
//         });
//
//     }
// }