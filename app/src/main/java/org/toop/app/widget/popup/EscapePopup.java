package org.toop.app.widget.popup;

import javafx.geometry.Pos;
import org.toop.app.App;
import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;

public class EscapePopup extends PopupWidget {
    public EscapePopup() {
        var confirmWidget = new ConfirmWidget("are-you-sure");

        confirmWidget.addButton("yes", () -> {
            App.quit();
        });

        confirmWidget.addButton("no", () -> {
            hide();
        });

        add(Pos.CENTER, confirmWidget);

        setOnPop(() -> {
            hide();
        });
    }
}
