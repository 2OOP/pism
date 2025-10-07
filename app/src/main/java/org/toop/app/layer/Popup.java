package org.toop.app.layer;

import org.toop.app.App;

public abstract class Popup extends Layer {
    protected Popup(boolean popOnBackground, String... backgroundStyles) {
        super(backgroundStyles);

        if (popOnBackground) {
            background.setOnMouseClicked(
                    _ -> {
                        App.pop();
                    });
        }
    }

    protected Popup(boolean popOnBackground) {
        this(popOnBackground, "bg-popup");
    }
}
