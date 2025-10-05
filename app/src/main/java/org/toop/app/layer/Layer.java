package org.toop.app.layer;

import org.toop.app.App;
import org.toop.app.canvas.GameCanvas;
import org.toop.framework.asset.ResourceManager;
import org.toop.framework.asset.resources.CssAsset;

import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public abstract class Layer {
	protected StackPane layer;
	protected Region background;

	protected Layer(String cssFile) {
		layer = new StackPane();
		layer.getStylesheets().add(ResourceManager.get(CssAsset.class, cssFile).getUrl());

		background = new Region();
		background.getStyleClass().add("background");
		background.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

		layer.getChildren().addLast(background);
	}

	protected void addContainer(Container container, Pos position, int xOffset, int yOffset, int widthPercent, int heightPercent) {
		StackPane.setAlignment(container.getContainer(), position);

		final double widthUnit = App.getWidth() / 100.0;
		final double heightUnit = App.getHeight() / 100.0;

		if (widthPercent > 0) {
			container.getContainer().setMaxWidth(widthPercent * widthUnit);
		} else {
			container.getContainer().setMaxWidth(Region.USE_PREF_SIZE);
		}

		if (heightPercent > 0) {
			container.getContainer().setMaxHeight(heightPercent * heightUnit);
		} else {
			container.getContainer().setMaxHeight(Region.USE_PREF_SIZE);
		}

		container.getContainer().setTranslateX(xOffset * widthUnit);
		container.getContainer().setTranslateY(yOffset * heightUnit);

		layer.getChildren().addLast(container.getContainer());
	}

	protected void addGameCanvas(GameCanvas canvas, Pos position, int xOffset, int yOffset) {
		StackPane.setAlignment(canvas.getCanvas(), position);

		final double widthUnit = App.getWidth() / 100.0;
		final double heightUnit = App.getHeight() / 100.0;

		canvas.getCanvas().setTranslateX(xOffset * widthUnit);
		canvas.getCanvas().setTranslateY(yOffset * heightUnit);

		layer.getChildren().addLast(canvas.getCanvas());
	}

	protected void pop() {
		if (layer.getChildren().size() <= 1) {
			return;
		}

		layer.getChildren().removeLast();
	}

	protected void popAll() {
		final int containers = layer.getChildren().size();

		for (int i = 1; i < containers; i++) {
			layer.getChildren().removeLast();
		}
	}

	public StackPane getLayer() {
		return layer;
	}

	public abstract void reload();
}