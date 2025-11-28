package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.PopupWidget;

public class ShowEnableTutorialWidget extends PopupWidget {

    public ShowEnableTutorialWidget(String text, Runnable onYes, Runnable onNo, Runnable onNever) {
        var a = Primitive.hbox(
                Primitive.button("ok", () -> { onYes.run(); this.hide(); }),
                Primitive.button("no", () -> { onNo.run(); this.hide(); }),
                Primitive.button("never", () -> { onNever.run(); this.hide(); })
        );

        var txt = Primitive.text(text);
        add(Pos.CENTER, Primitive.vbox(txt, a));
        WidgetContainer.add(Pos.CENTER, this);
    }
}