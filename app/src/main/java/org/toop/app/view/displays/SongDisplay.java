package org.toop.app.view.displays;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.toop.framework.audio.AudioEventListener;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import org.toop.framework.eventbus.GlobalEventBus;

public class SongDisplay extends VBox {

    private final Text songTitle;
    private final ProgressBar progressBar;
    private final Text progressText;

    public SongDisplay() {
        new EventFlow()
                .listen(this::updateTheSong);

        setAlignment(Pos.CENTER);
        getStyleClass().add("song-display");

        // TODO ADD GOOD SONG TITLES WITH ARTISTS DISPLAYED
        songTitle = new Text("song playing");
        songTitle.getStyleClass().add("song-title");

        progressBar = new ProgressBar(0);
        progressBar.getStyleClass().add("progress-bar");

        progressText = new Text("0:00/0:00");
        progressText.getStyleClass().add("progress-text");

        Button skipButton = new Button(">>");
        skipButton.getStyleClass().setAll("skip-button");
        skipButton.setOnAction( event -> {
            GlobalEventBus.post(new AudioEvents.SkipMusic());
        });

        getChildren().addAll(songTitle, progressBar, progressText, skipButton);
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
}





