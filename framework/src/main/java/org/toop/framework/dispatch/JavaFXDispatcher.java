package org.toop.framework.dispatch;

import javafx.application.Platform;
import org.toop.framework.dispatch.interfaces.Dispatcher;

public class JavaFXDispatcher implements Dispatcher {
    @Override
    public void run(Runnable task) {
        Platform.runLater(task);
    }
}