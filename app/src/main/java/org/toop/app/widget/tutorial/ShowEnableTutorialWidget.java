package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.PopupWidget;

public class ShowEnableTutorialWidget extends PopupWidget {

    public ShowEnableTutorialWidget(String text, Runnable onYes, Runnable onNo, Runnable onNever) {
        var a = Primitive.hbox(
                Primitive.button("ok", onYes),
                Primitive.button("no", onNo),
                Primitive.button("never", onNever)
        );

        var txt = Primitive.text(text);
        add(Pos.CENTER, Primitive.vbox(txt, a));
        WidgetContainer.add(Pos.CENTER, this);
    }
}