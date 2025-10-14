package org.toop.app.layer.layers;

import javafx.geometry.Pos;
import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.framework.audio.VolumeControl;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;
import org.toop.app.layer.layers.game.ReversiLayer;
import org.toop.local.AppContext;

public final class MainLayer extends Layer {
    public MainLayer() {
        super("bg-primary");
        reload();
    }

    @Override
    public void reload() {
        popAll();

        final var tictactoeButton =
                NodeBuilder.button(
                        AppContext.getString("tictactoe"),
                        () -> {
                            App.activate(new MultiplayerLayer());
                        });

		final var othelloButton =
                NodeBuilder.button(
                        AppContext.getString("othello"),
                        () -> {
                            App.activate(new ReversiLayer());
		});

        final var creditsButton =
                NodeBuilder.button(
                        AppContext.getString("credits"),
                        () -> {
                            App.push(new CreditsPopup());
                        });

        final var optionsButton =
                NodeBuilder.button(
                        AppContext.getString("options"),
                        () -> {
                            App.push(new OptionsPopup());
                        });

        final var quitButton =
                NodeBuilder.button(
                        AppContext.getString("quit"),
                        () -> {
                            App.quitPopup();
                        });

        final Container gamesContainer = new VerticalContainer(5);
        gamesContainer.addNodes(tictactoeButton, othelloButton);

        final Container controlContainer = new VerticalContainer(5);
        controlContainer.addNodes(creditsButton, optionsButton, quitButton);

        addContainer(gamesContainer, Pos.TOP_LEFT, 2, 2, 20, 0);
        addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 20, 0);
    }
}