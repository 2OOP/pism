package org.toop.app.layer.layers.game;

import javafx.geometry.Pos;
import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.Popup;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.app.layer.layers.MainLayer;
import org.toop.local.AppContext;

public class GameFinishedPopup extends Popup {
    private final boolean isDraw;
    private final String winner;

    public GameFinishedPopup(boolean isDraw, String winner) {
        super(true, "bg-popup");

        this.isDraw = isDraw;
        this.winner = winner;

        reload();
    }

    @Override
    public void reload() {
        popAll();

        final Container mainContainer = new VerticalContainer(30);

        if (isDraw) {
            final var drawHeader = NodeBuilder.header(AppContext.getString("drawText"));
            final var goodGameText = NodeBuilder.text(AppContext.getString("goodGameText"));

            mainContainer.addNodes(drawHeader, goodGameText);
        } else {
            final var winHeader =
                    NodeBuilder.header(AppContext.getString("congratulations") + ": " + winner);
            final var goodGameText = NodeBuilder.text(AppContext.getString("goodGameText"));

            mainContainer.addNodes(winHeader, goodGameText);
        }

        final var backToMainMenuButton =
                NodeBuilder.button(
                        AppContext.getString("backToMainMenu"),
                        () -> {
                            App.activate(new MainLayer());
                        });

        mainContainer.addNodes(backToMainMenuButton);

        addContainer(mainContainer, Pos.CENTER, 0, 0, 30, 30);
    }
}
