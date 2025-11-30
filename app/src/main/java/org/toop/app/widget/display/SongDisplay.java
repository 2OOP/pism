package org.toop.app.widget.display;

import org.toop.app.widget.Widget;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.eventbus.GlobalEventBus;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class SongDisplay extends VBox implements Widget {
	private final Text songTitle;
	private final ProgressBar progressBar;
	private final Text progressText;

	public SongDisplay() {
		new EventFlow()
				.listen(AudioEvents.PlayingMusic.class, this::updateTheSong, false);

		setAlignment(Pos.CENTER);
		setMaxHeight(Region.USE_PREF_SIZE);
		getStyleClass().add("song-display");

		// TODO ADD GOOD SONG TITLES WITH ARTISTS DISPLAYED
		songTitle = new Text("song playing");
		songTitle.getStyleClass().add("song-title");

		progressBar = new ProgressBar(0);
		progressBar.getStyleClass().add("progress-bar");

		progressText = new Text("0:00/0:00");
		progressText.getStyleClass().add("progress-text");

		// TODO ADD BETTER CSS FOR THE SKIPBUTTON WHERE ITS AT A NICER POSITION

		Button skipButton = new Button(">>");
		Button pauseButton = new Button("⏸");
		Button previousButton = new Button("<<");

		skipButton.getStyleClass().setAll("skip-button");
		pauseButton.getStyleClass().setAll("pause-button");
		previousButton.getStyleClass().setAll("previous-button");

		skipButton.setOnAction( event -> {
			GlobalEventBus.post(new AudioEvents.SkipMusic());
		});

		pauseButton.setOnAction(event -> {
			GlobalEventBus.post(new AudioEvents.PauseMusic());
			if (pauseButton.getText().equals("⏸")) {
				pauseButton.setText("▶");
			}
			else if (pauseButton.getText().equals("▶")) {
				pauseButton.setText("⏸");
			}
		});

		previousButton.setOnAction( event -> {
			GlobalEventBus.post(new AudioEvents.PreviousMusic());
		});

		HBox control = new HBox(10, previousButton, pauseButton, skipButton);
		control.setAlignment(Pos.CENTER);
		control.getStyleClass().add("controls");

		getChildren().addAll(songTitle, progressBar, progressText, control);
	}

	private void updateTheSong(AudioEvents.PlayingMusic event) {
		Platform.runLater(() -> {
			String text = event.name();
			text = text.substring(0, text.length() - 4);
			songTitle.setText(text);
			double currentPos = event.currentPosition();
			double duration = event.duration();
			if (currentPos / duration > 0.05) {
				double progress = currentPos / duration;
				progressBar.setProgress(progress);
			}
			else if (currentPos / duration < 0.05) {
				progressBar.setProgress(0.05);
			}
			progressText.setText(getTimeString(event.currentPosition(), event.duration()));
		});
	}

	private String getTimeString(long position, long duration) {
		long positionMinutes = position / 60;
		long durationMinutes = duration / 60;
		long positionSeconds = position % 60;
		long durationSeconds = duration % 60;
		String positionSecondsStr = String.valueOf(positionSeconds);
		String durationSecondsStr = String.valueOf(durationSeconds);

		if (positionSeconds < 10) {
			positionSecondsStr  = "0" + positionSeconds;
		}
		if (durationSeconds < 10) {
			durationSecondsStr = "0" + durationSeconds;
		}

		String time = positionMinutes + ":" + positionSecondsStr + " / " + durationMinutes + ":" + durationSecondsStr;
		return time;
	}

	@Override
	public Node getNode() {
		return this;
	}
}