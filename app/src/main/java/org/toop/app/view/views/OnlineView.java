package org.toop.app.view.views;

import org.toop.app.Server;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.local.AppContext;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class OnlineView extends View {
	public OnlineView() {
		super(true, "bg-primary");
	}

	@Override
	public void setup() {
		final Text serverInformationHeader = header();
		serverInformationHeader.setText(AppContext.getString("server-information"));

		final Text serverIPText = text();
		serverIPText.setText(AppContext.getString("ip-address"));

		final TextField serverIPInput = input();
		serverIPInput.setPromptText(AppContext.getString("enter-the-server-ip"));

		final Text serverPortText = text();
		serverPortText.setText(AppContext.getString("port"));

		final TextField serverPortInput = input();
		serverPortInput.setPromptText(AppContext.getString("enter-the-server-port"));

		final Text playerNameText = text();
		playerNameText.setText(AppContext.getString("player-name"));

		final TextField playerNameInput = input();
		playerNameInput.setPromptText(AppContext.getString("enter-your-name"));

		final Button connectButton = button();
		connectButton.setText(AppContext.getString("connect"));
		connectButton.setOnAction(_ -> {
			new Server(serverIPInput.getText(), serverPortInput.getText(), playerNameInput.getText());
		});

		add(Pos.CENTER,
			fit(vboxFill(
				serverInformationHeader,
				separator(),

				vboxFill(
					serverIPText,
					serverIPInput
				),

				vboxFill(
					serverPortText,
					serverPortInput
				),

				vboxFill(
					playerNameText,
					playerNameInput
				),

				vboxFill(
					connectButton
				)
			))
		);

		final Button backButton = button();
		backButton.setText(AppContext.getString("back"));
		backButton.setOnAction(_ -> { ViewStack.push(new MainView()); });

		add(Pos.BOTTOM_LEFT,
			vboxFill(
				backButton
			)
		);
	}
}