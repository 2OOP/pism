package org.toop.app.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.toop.app.App;

public final class QuitMenu extends Menu {
	public QuitMenu() {
		final Region background = new Region();
		background.getStyleClass().add("quit-background");
		background.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

		final Text sure = new Text("Are you sure?");
		sure.getStyleClass().add("quit-text");

		final Button yes = new Button("Yes");
		yes.getStyleClass().add("quit-button");
		yes.setOnAction(_ -> {
			App.quit();
		});

		final Button no = new Button("No");
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
		pane.getStylesheets().add(getClass().getResource("/style/quit.css").toExternalForm());
	}
}