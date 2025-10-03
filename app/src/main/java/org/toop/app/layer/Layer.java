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

	protected Layer(String cssFile, String backgroundCssClass) {
		layer = new StackPane();
		layer.setPickOnBounds(false);
		layer.getStylesheets().add(ResourceManager.get(CssAsset.class, cssFile).getUrl());

		background = new Region();
		background.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
		background.getStyleClass().add(backgroundCssClass);

		layer.getChildren().addLast(background);
	}

	protected Layer(String cssFile) {
		this(cssFile, "background");
	}

	protected void addContainer(Container container, Pos position, int xOffset, int yOffset) {
		StackPane.setAlignment(container.getContainer(), position);

		container.getContainer().setMaxWidth(Region.USE_PREF_SIZE);
		container.getContainer().setMaxHeight(Region.USE_PREF_SIZE);

		final double xPercent = xOffset * (App.getWidth() / 100.0);
		final double yPercent = yOffset * (App.getHeight() / 100.0);

		container.getContainer().setTranslateX(xPercent);
		container.getContainer().setTranslateY(yPercent);

		layer.getChildren().addLast(container.getContainer());
	}

	protected void addCanvas(GameCanvas canvas, Pos position, int xOffset, int yOffset) {
		StackPane.setAlignment(canvas.getCanvas(), position);

		final double xPercent = xOffset * (App.getWidth() / 100.0);
		final double yPercent = yOffset * (App.getHeight() / 100.0);

		canvas.getCanvas().setTranslateX(xPercent);
		canvas.getCanvas().setTranslateX(yPercent);

		layer.getChildren().addLast(canvas.getCanvas());
	}

	public StackPane getLayer() { return layer; }
	public Region getBackground() { return background; }
}