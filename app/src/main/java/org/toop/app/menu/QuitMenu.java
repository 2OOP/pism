package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.toop.app.App;
import org.toop.framework.asset.AssetManager;
import org.toop.framework.asset.resources.CssAsset;
import org.toop.local.AppContext;

import java.util.Locale;
import java.util.ResourceBundle;

public final class QuitMenu extends Menu {
    private Locale currentLocale = AppContext.getLocale();
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("Localization", currentLocale);
    public QuitMenu() {
		final Region background = new Region();
		background.getStyleClass().add("quit-background");
		background.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

		final Text sure = new Text(resourceBundle.getString("quitMenuTextSure"));
		sure.getStyleClass().add("quit-text");

		final Button yes = new Button(resourceBundle.getString("quitMenuButtonYes"));
		yes.getStyleClass().add("quit-button");
		yes.setOnAction(_ -> {
			App.quit();
		});

		final Button no = new Button(resourceBundle.getString("quitMenuButtonNo"));
		no.getStyleClass().add("quit-button");
		no.setOnAction(_ -> {
			App.pop();
		});

		final HBox buttons = new HBox(10, yes, no);
		buttons.setAlignment(Pos.CENTER);

		VBox box = new VBox(43, sure, buttons);
		box.setAlignment(Pos.CENTER);
		box.getStyleClass().add("quit-box");
		box.setMaxWidth(350);
		box.setMaxHeight(200);

		StackPane modalContainer = new StackPane(background, box);
		StackPane.setAlignment(box, Pos.CENTER);

		pane = modalContainer;
		CssAsset css = AssetManager.get("quit.css");
		pane.getStylesheets().add(css.getUrl());
	}
}