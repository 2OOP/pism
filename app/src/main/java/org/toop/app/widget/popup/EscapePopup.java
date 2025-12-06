package org.toop.app.widget.popup;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.toop.app.App;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.ConfirmWidget;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.app.widget.complex.ViewWidget;
import org.toop.app.widget.tutorial.*;
import org.toop.app.widget.view.GameView;
import org.toop.app.widget.view.OptionsView;
import org.toop.local.AppContext;

import java.awt.*;
import java.util.ArrayList;

public class EscapePopup extends PopupWidget {
    public EscapePopup() {
        ArrayList<Node> nodes = new ArrayList<>();
        ViewWidget currentView = WidgetContainer.getCurrentView();

        nodes.add(Primitive.button("Continue", this::hide, false)); // TODO, localize

        if (!(currentView.getClass().isAssignableFrom(OptionsView.class))) {
            var opt = Primitive.button("options", () -> {
                hide();
                WidgetContainer.getCurrentView().transitionNext(new OptionsView());
            });
            nodes.add(opt);
        }

        if (currentView.getClass().isAssignableFrom(GameView.class)) {
            BaseTutorialWidget tut = AppContext.currentTutorial();
            if (tut != null) {
                nodes.add(Primitive.button("tutorialstring", () -> {}));
            }
        }

        nodes.add(Primitive.button("quit", () -> {
            hide();
            WidgetContainer.add(Pos.CENTER, new QuitPopup());
        }));

        add(Pos.CENTER, Primitive.vbox(nodes.toArray(new Node[0])));

    }
}
