package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.GameInformation;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.game.TicTacToeLayer;
import org.toop.local.AppContext;

import javafx.geometry.Pos;

import java.time.LocalDateTime;

public final class MultiplayerLayer extends Layer {
	private boolean isConnectionLocal = true;

	private boolean isPlayer1Human = true;
	private String player1Name = "";
	private int computer1Difficulty = 0;
	private int computer1ThinkTime = 0;

	private boolean isPlayer2Human = true;
	private String player2Name = "";
	private int computer2Difficulty = 0;
	private int computer2ThinkTime = 0;

	private String serverIP = "";
	private String serverPort = "";

	public MultiplayerLayer() {
		super("bg-primary");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container player1Container = new VerticalContainer(20);
		final Container player2Container = new VerticalContainer(20);

		final var isPlayer1HumanToggle = NodeBuilder.toggle(AppContext.getString("human"), AppContext.getString("computer"), !isPlayer1Human, (computer) -> {
			isPlayer1Human = !computer;
			reload();
		});

		player1Container.addNodes(isPlayer1HumanToggle);

		if (isPlayer1Human) {
			final var playerNameText = NodeBuilder.text(AppContext.getString("playerName"));
			final var playerNameInput = NodeBuilder.input(player1Name, (name) -> {
				player1Name = name;
			});

			player1Container.addNodes(playerNameText, playerNameInput);
		} else {
			player1Name = "Pism Bot V" + LocalDateTime.now().getSecond();

			final var computerNameText = NodeBuilder.text(player1Name);
			final var computerNameSeparator = NodeBuilder.separator();

			final var computerDifficultyText = NodeBuilder.text(AppContext.getString("computerDifficulty"));
			final var computerDifficultySeparator = NodeBuilder.separator();
			final var computerDifficultySlider = NodeBuilder.slider(10, computer1Difficulty, (difficulty) ->
					computer1Difficulty = difficulty);

			final var computerThinkTimeText = NodeBuilder.text(AppContext.getString("computerThinkTime"));
			final var computerThinkTimeSlider = NodeBuilder.slider(5, computer1ThinkTime, (thinkTime) ->
					computer1ThinkTime = thinkTime);

			player1Container.addNodes(computerNameText, computerNameSeparator,
					computerDifficultyText, computerDifficultySlider, computerDifficultySeparator,
					computerThinkTimeText, computerThinkTimeSlider);
		}

		if (isConnectionLocal) {
			final var isPlayer2HumanToggle = NodeBuilder.toggle(AppContext.getString("human"), AppContext.getString("computer"), !isPlayer2Human, (computer) -> {
				isPlayer2Human = !computer;
				reload();
			});

			player2Container.addNodes(isPlayer2HumanToggle);

			if (isPlayer2Human) {
				final var playerNameText = NodeBuilder.text(AppContext.getString("playerName"));
				final var playerNameInput = NodeBuilder.input(player2Name, (name) -> {
					player2Name = name;
				});

				player2Container.addNodes(playerNameText, playerNameInput);
			} else {
				player2Name = "Pism Bot V" + LocalDateTime.now().getSecond();

				final var computerNameText = NodeBuilder.text(player2Name);
				final var computerNameSeparator = NodeBuilder.separator();

				final var computerDifficultyText = NodeBuilder.text(AppContext.getString("computerDifficulty"));
				final var computerDifficultySeparator = NodeBuilder.separator();
				final var computerDifficultySlider = NodeBuilder.slider(10, computer2Difficulty, (difficulty) ->
						computer2Difficulty = difficulty);

				final var computerThinkTimeText = NodeBuilder.text(AppContext.getString("computerThinkTime"));
				final var computerThinkTimeSlider = NodeBuilder.slider(5, computer2ThinkTime, (thinkTime) ->
						computer2ThinkTime = thinkTime);

				player2Container.addNodes(computerNameText, computerNameSeparator,
						computerDifficultyText, computerDifficultySlider, computerDifficultySeparator,
						computerThinkTimeText, computerThinkTimeSlider);
			}
		} else {
			final var serverIPText = NodeBuilder.text(AppContext.getString("serverIP"));
			final var serverIPSeparator = NodeBuilder.separator();
			final var serverIPInput = NodeBuilder.input(serverIP, (ip) -> {
				serverIP = ip;
			});

			final var serverPortText = NodeBuilder.text(AppContext.getString("serverPort"));
			final var serverPortInput = NodeBuilder.input(serverPort, (port) -> {
				serverPort = port;
			});

			player2Container.addNodes(serverIPText, serverIPInput, serverIPSeparator,
					serverPortText, serverPortInput);
		}

		final var versusText = NodeBuilder.header("VS");

		final var connectionTypeText = NodeBuilder.text(AppContext.getString("connectionType") + ":");
		final var connectionTypeToggle = NodeBuilder.toggle(AppContext.getString("local"), AppContext.getString("server"), !isConnectionLocal, (server) -> {
			isConnectionLocal = !server;
			reload();
		});

		final var playButton = NodeBuilder.button(isConnectionLocal ? AppContext.getString("start") : AppContext.getString("connect"), () -> {
			final var information = new GameInformation(
					new String[]{player1Name, player2Name},
					new boolean[]{isPlayer1Human, isPlayer2Human},
					new int[]{computer1Difficulty, computer2Difficulty},
					new int[]{computer1ThinkTime, computer2ThinkTime},
					isConnectionLocal, serverIP, serverPort);

			if (isConnectionLocal) {
				App.activate(new TicTacToeLayer(information));
			} else {
				App.activate(new ConnectedLayer(information));
			}
		});

		final Container mainContainer = new VerticalContainer(10);
		final Container playersContainer = new HorizontalContainer(5);
		final Container connectionTypeContainer = new HorizontalContainer(10);

		mainContainer.addContainer(playersContainer, true);
		mainContainer.addContainer(connectionTypeContainer, false);
		mainContainer.addNodes(playButton);

		connectionTypeContainer.addNodes(connectionTypeText, connectionTypeToggle);

		playersContainer.addContainer(player1Container, true);
		playersContainer.addNodes(versusText);
		playersContainer.addContainer(player2Container, true);

		final var backButton = NodeBuilder.button(AppContext.getString("back"), () -> {
			App.activate(new MainLayer());
		});

		final Container controlContainer = new VerticalContainer(0);
		controlContainer.addNodes(backButton);

		addContainer(mainContainer, Pos.CENTER, 0, 0, 75, 75);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}