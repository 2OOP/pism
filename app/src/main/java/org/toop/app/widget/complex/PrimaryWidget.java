package org.toop.app.widget.complex;

import javafx.geometry.Pos;
import org.toop.app.widget.WidgetContainer;

public abstract class PrimaryWidget extends ViewWidget implements TransitionAnimation {
    public PrimaryWidget() {
        super("bg-primary");
    }

    @Override
    public void transition(PrimaryWidget primary) {
        WidgetContainer.add(Pos.CENTER, primary);
        WidgetContainer.remove(this);
    }
}