package org.toop.app.widget.primary;

import org.toop.app.App;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.PrimaryWidget;

import javafx.geometry.Pos;

public class MainPrimary extends PrimaryWidget {
	public MainPrimary() {
		var localButton = Primitive.button("local", () -> {
			transitionNext(new LocalPrimary());
		});

		var onlineButton = Primitive.button("online", () -> {
			transitionNext(new OnlinePrimary());
		});

		var creditsButton = Primitive.button("credits", () -> {
			transitionNext(new CreditsPrimary());
		});

		var optionsButton = Primitive.button("options", () -> {
			transitionNext(new OptionsPrimary());
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