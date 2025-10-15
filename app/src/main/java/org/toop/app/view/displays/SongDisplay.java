package org.toop.app.view.displays;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import org.toop.app.App;
import org.toop.app.view.View;
import org.toop.framework.audio.AudioEventListener;
import org.toop.framework.resource.ResourceManager;
import org.toop.framework.resource.resources.MusicAsset;
import org.toop.local.AppContext;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.toop.framework.audio.MusicManager;

import java.util.ArrayList;
import java.util.List;

public class SongDisplay extends VBox {

    private final Text songTitle;
    private final ProgressBar progressBar;
    private final Text progressText;
    private MusicManager<MusicAsset> manager;

    public SongDisplay() {

        setAlignment(Pos.CENTER);
        getStyleClass().add("song-display");

        songTitle = new Text("song playing");
        songTitle.getStyleClass().add("song-title");

        progressBar = new ProgressBar(0);
        progressBar.getStyleClass().add("progress-bar");

        progressText = new Text("THIS IS AN EXAMPLE CUZ BAS DIDNT DECIDE TO MAKE EVENTS");
        progressText.getStyleClass().add("progress-text");

        getChildren().addAll(songTitle, progressBar, progressText);

        setMusicManager();
    }

    public void updateTheSong(String title, double progress) {
        songTitle.setText(title);
        progressBar.setProgress(progress);
    }

    public String getPlayingSong() {
        return manager.getCurrentTrack().getName();
    }

    public void setMusicManager() {
        List<MusicAsset> tracks = ResourceManager.getAllOfTypeAndRemoveWrapper(MusicAsset.class);
        this.manager = new MusicManager<>(tracks);

        String song = getPlayingSong();
        Platform.runLater(() -> songTitle.setText(song));
    }
}




