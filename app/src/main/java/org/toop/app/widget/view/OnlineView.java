package org.toop.app.widget.view;

import org.toop.app.Server;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.LabeledInputWidget;
import org.toop.app.widget.complex.ViewWidget;

import javafx.geometry.Pos;
import org.toop.framework.game.games.reversi.BitboardReversi;
import org.toop.framework.game.games.tictactoe.BitboardTicTacToe;
import org.toop.framework.gameFramework.model.game.TurnBasedGame;
import org.toop.framework.networking.server.MasterServer;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

public class OnlineView extends ViewWidget {
	public OnlineView() {
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

		var localHostButton = Primitive.button("host!", () -> {
			var games = new ConcurrentHashMap<String, Class<? extends TurnBasedGame>>();
			games.put("tic-tac-toe", BitboardTicTacToe.class);
			games.put("reversi", BitboardReversi.class);

			var a = new MasterServer(6666, games, Duration.ofSeconds(10));

			new Thread(() -> {
				try {
					a.start();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}).start();

			new Server(
					"127.0.0.1",
					"6666",
					"host",
					a
			);
		}, false);

		add(Pos.CENTER, Primitive.vbox(
			serverInformationHeader,
			Primitive.separator(),

			serverIPInput.getNode(),
			serverPortInput.getNode(),
			playerNameInput.getNode(),
			Primitive.separator(),

			connectButton,
			Primitive.separator(),
			localHostButton
		));
	}
}