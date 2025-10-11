package org.toop.framework.audio;

import javafx.application.Platform;
import org.toop.framework.audio.interfaces.Dispatcher;

// TODO isn't specific to audio
public class JavaFXDispatcher implements Dispatcher {
    @Override
    public void run(Runnable task) {
        Platform.runLater(task);
    }
}