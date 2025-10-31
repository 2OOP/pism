package org.toop.app.widget.primary;

import org.toop.app.Server;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.LabeledInputWidget;
import org.toop.app.widget.complex.PrimaryWidget;

import javafx.geometry.Pos;

public class OnlinePrimary extends PrimaryWidget {
	public OnlinePrimary() {
		var serverInformationHeader = Primitive.header("server-information");

		var serverIPInput = new LabeledInputWidget("ip-address", "enter-the-server-ip", "", _ -> {});
		var serverPortInput = new LabeledInputWidget("port", "enter-the-server-port", "", _ -> {});
		var playerNameInput = new LabeledInputWidget("player-name", "enter-your-name", "", _ -> {});

		var connectButton = Primitive.button("connect", () -> {
			new Server(
				serverIPInput.getValue(),
				serverPortInput.getValue(),
				playerNameInput.getValue()
			);
		});

		add(Pos.CENTER, Primitive.vbox(
			serverInformationHeader,
			Primitive.separator(),

			serverIPInput.getNode(),
			serverPortInput.getNode(),
			playerNameInput.getNode(),
			Primitive.separator(),

			connectButton
		));
	}
}