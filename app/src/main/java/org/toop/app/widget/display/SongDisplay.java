package org.toop.app.widget.display;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
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

import java.util.Timer;

public class SongDisplay extends VBox implements Widget {
	private final Text songTitle;
	private final ProgressBar progressBar;
	private final Text progressText;
    private boolean canClick = true;

	public SongDisplay() {
		new EventFlow()
			.listen(this::updateTheSong);

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
            if (!canClick) { return; }
			GlobalEventBus.post(new AudioEvents.SkipMusic());
            doCooldown();
		});

		pauseButton.setOnAction(event -> {
            if (!canClick) { return; }
			GlobalEventBus.post(new AudioEvents.PauseMusic());
			if (pauseButton.getText().equals("⏸")) {
				pauseButton.setText("▶");
			}
			else if (pauseButton.getText().equals("▶")) {
				pauseButton.setText("⏸");
			}
            doCooldown();
		});

		previousButton.setOnAction( event -> {
            if (!canClick) { return; }
			GlobalEventBus.post(new AudioEvents.PreviousMusic());
            doCooldown();
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

    private void doCooldown() {
        canClick = false;
        Timeline cooldown = new Timeline(
                new KeyFrame(Duration.millis(300), event -> canClick = true)
        );
        cooldown.setCycleCount(1);
        cooldown.play();
    }


	@Override
	public Node getNode() {
		return this;
	}
}