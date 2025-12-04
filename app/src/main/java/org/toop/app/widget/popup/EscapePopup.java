package org.toop.app.widget.popup;

import javafx.geometry.Pos;
import org.toop.app.App;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.app.widget.view.OptionsView;

public class EscapePopup extends PopupWidget {
    public EscapePopup() {
        var con = Primitive.button("Continue", () -> { hide(); }, false); // TODO, localize

        var qui = Primitive.button("quit", () -> {
            hide();
            WidgetContainer.add(Pos.CENTER, new QuitPopup());
        });

        if (!(WidgetContainer.getCurrentView().getClass().isAssignableFrom(OptionsView.class))) {
            var opt = Primitive.button("options", () -> {
                hide();
                WidgetContainer.getCurrentView().transitionNext(new OptionsView());
            });
            add(Pos.CENTER, Primitive.vbox(con, opt, qui));
        } else {
            add(Pos.CENTER, Primitive.vbox(con, qui));
        }

    }
}
