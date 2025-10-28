package org.toop.app.widget.complex;

import javafx.scene.layout.HBox;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;

import javafx.scene.layout.VBox;

public class ConfirmWidget implements Widget<VBox> {
    private final HBox buttonsContainer;
    private final VBox container;

    public ConfirmWidget(String confirm) {
        buttonsContainer = Primitive.hbox();
        container = Primitive.vbox(Primitive.text(confirm), buttonsContainer);
    }

    public void addButton(String label, Runnable onClick) {
        var button = Primitive.button(label);
        button.setOnAction(_ -> onClick.run());
        buttonsContainer.getChildren().add(button);
    }

    @Override
    public VBox getNode() { return container; }
}