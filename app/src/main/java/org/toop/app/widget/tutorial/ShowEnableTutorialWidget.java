package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.PopupWidget;

public class ShowEnableTutorialWidget extends PopupWidget {
//    private final Button yesButton;
//    private final Button noButton;
//    private final Button neverButton;

    public ShowEnableTutorialWidget(String words, Runnable onYes, Runnable onNo, Runnable onNever) {
//        this.yesButton = Primitive.button("ok", onYes);
//        this.noButton = Primitive.button("no", onNo);
//        this.neverButton = Primitive.button("never", onNever);

        var a = Primitive.hbox(
                Primitive.button("ok", onYes),
                Primitive.button("no", onNo),
                Primitive.button("never", onNever)
        );

        var txt = Primitive.text(words);
        add(Pos.CENTER, Primitive.vbox(txt, a));
        WidgetContainer.add(Pos.CENTER, this);
    }
}