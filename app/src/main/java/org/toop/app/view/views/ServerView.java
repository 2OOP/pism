package org.toop.app.view.views;

import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.app.view.displays.SongDisplay;
import org.toop.local.AppContext;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;

public final class ServerView extends View {
	private final String user;
	private final Consumer<String> onPlayerClicked;
	private final Runnable onDisconnect;

	private ListView<Button> listView;

	public ServerView(String user, Consumer<String> onPlayerClicked, Runnable onDisconnect) {
		super(true, "bg-primary");

		this.user = user;
		this.onPlayerClicked = onPlayerClicked;
		this.onDisconnect = onDisconnect;
	}

	public void update(List<String> players) {
		Platform.runLater(() -> {
			listView.getItems().clear();

			for (int i = 0; i < players.size(); i++) {
				final int finalI = i;

				final Button button = button();
				button.setText(players.get(i));
				button.setOnAction(_ -> {
					onPlayerClicked.accept(players.get(finalI));
				});

				listView.getItems().add(button);
			}
		});
	}

	@Override
	public void setup() {
		final Text playerHeader = header();
		playerHeader.setText(user);

		listView = new ListView<Button>();

        final SongDisplay songdisplay = new SongDisplay();


        add(Pos.BOTTOM_RIGHT,
                fit(vboxFill(
                        songdisplay
                )));

		add(Pos.CENTER,
			fit(vboxFill(
				vbox(
					playerHeader,
					separator()
				),

				listView
			))
		);

		final Button disconnectButton = button();
		disconnectButton.setText(AppContext.getString("disconnect"));
		disconnectButton.setOnAction(_ -> {
			onDisconnect.run();
			ViewStack.push(new OnlineView());
		});

		add(Pos.BOTTOM_LEFT,
			vboxFill(
				disconnectButton
			)
		);
	}
}