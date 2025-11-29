package org.toop.app.widget.view;

import org.toop.app.App;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;

import javafx.geometry.Pos;

public class MainView extends ViewWidget {
	public MainView() {
		var localButton = Primitive.button("local", () -> {
			transitionNext(new LocalView());
		});

		var onlineButton = Primitive.button("online", () -> {
			transitionNext(new OnlineView());
		});

		var creditsButton = Primitive.button("credits", () -> {
			transitionNext(new CreditsView());
		});

		var optionsButton = Primitive.button("options", () -> {
			transitionNext(new OptionsView());
		});

		var quitButton = Primitive.button("quit", () -> {
			App.startQuit();
		});

		add(Pos.CENTER, Primitive.vbox(
			localButton,
			onlineButton,
			creditsButton,
			optionsButton,
			quitButton
		));
	}
}