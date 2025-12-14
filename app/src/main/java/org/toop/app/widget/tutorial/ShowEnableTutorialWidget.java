package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.local.AppSettings;

public class ShowEnableTutorialWidget extends PopupWidget {

    public ShowEnableTutorialWidget(Runnable tutorial, Runnable nextScreen, Runnable appSettingsSetter) {
        var a = Primitive.hbox(
                Primitive.button("ok", () -> { appSettingsSetter.run(); tutorial.run(); this.hide(); }, false),
                Primitive.button("no", () -> { appSettingsSetter.run(); nextScreen.run(); this.hide(); }, false),
                Primitive.button("never", () -> { AppSettings.getSettings().setTutorialFlag(false); nextScreen.run(); this.hide(); }, false)
        );

        var txt = Primitive.text("tutorial");
        add(Pos.CENTER, Primitive.vbox(txt, a));
        WidgetContainer.add(Pos.CENTER, this);
    }
}