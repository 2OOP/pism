package org.toop.app.widget.view;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;
import javafx.geometry.Pos;
import org.toop.app.widget.popup.QuitPopup;

public class MainView extends ViewWidget {
	public MainView() {
		var localButton = Primitive.button("local", () -> {
			transitionNext(new LocalView());
		}, false);

		var onlineButton = Primitive.button("online", () -> {
			transitionNext(new OnlineView());
		}, false);

		var creditsButton = Primitive.button("credits", () -> {
			transitionNext(new CreditsView());
		}, false);

		var optionsButton = Primitive.button("options", () -> {
			transitionNext(new OptionsView());
		}, false);

		var quitButton = Primitive.button("quit", () -> {
			var a = new QuitPopup();
			a.show(Pos.CENTER);
		}, false);

		add(Pos.CENTER, Primitive.vbox(
			localButton,
			onlineButton,
			creditsButton,
			optionsButton,
			quitButton
		));
	}
}