package org.toop.app.view.views;

import org.toop.app.App;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.local.AppContext;
import org.toop.app.view.displays.SongDisplay;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

public final class MainView extends View {
	public MainView() {
		super(true, "bg-primary");
	}

	@Override
	public void setup() {
		final Button localButton = button();
		localButton.setText(AppContext.getString("local"));
		localButton.setOnAction(_ -> { ViewStack.push(new LocalView()); });

		final Button onlineButton = button();
		onlineButton.setText(AppContext.getString("online"));
		onlineButton.setOnAction(_ -> { ViewStack.push(new OnlineView()); });

		final Button creditsButton = button();
		creditsButton.setText(AppContext.getString("credits"));
		creditsButton.setOnAction(_ -> { ViewStack.push(new CreditsView()); });

		final Button optionsButton = button();
		optionsButton.setText(AppContext.getString("options"));
		optionsButton.setOnAction(_ -> { ViewStack.push(new OptionsView()); });

		final Button quitButton = button();
		quitButton.setText(AppContext.getString("quit"));
		quitButton.setOnAction(_ -> { App.startQuit(); });

        final SongDisplay songdisplay = new SongDisplay();

        add(Pos.BOTTOM_RIGHT,
            fit(vboxFill(
                    songdisplay
                    )));

		add(Pos.CENTER,
			fit(vboxFill(
				localButton,
				onlineButton,
				creditsButton,
				optionsButton,
				quitButton
			))
		);
	}
}