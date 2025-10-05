package org.toop.app.layer.layers;

import javafx.geometry.Pos;
import org.toop.app.App;
import org.toop.app.GameType;
import org.toop.app.Match;
import org.toop.app.canvas.TicTacToeCanvas;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.VerticalContainer;

import java.time.LocalDateTime;

public class GameLayer extends Layer {
    Match match;
	public GameLayer() {
		super("game.css");
        //temp
        match = new Match("name"+LocalDateTime.now().getSecond(),false,"127.0.0.1",7789, GameType.TICTACTOE);
		reload();
	}

	@Override
	public void reload() {
		App.popAll();

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addButton("Back", () -> { App.activate(new MainLayer()); });

		addCanvas(new TicTacToeCanvas(), Pos.CENTER, 0, 0, 100, 100);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);
	}
}