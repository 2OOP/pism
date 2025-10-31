package org.toop.app.widget.primary;

import org.toop.app.App;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.PrimaryWidget;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CreditsPrimary extends PrimaryWidget {
	public CreditsPrimary() {
		var scrumMasterCredit = newCredit("scrum-master", "Stef");
		var productOwnerCredit = newCredit("product-owner", "Omar");
		var mergeCommanderCredit = newCredit("merge-commander", "Bas");
		var localizationCredit = newCredit("localization", "Ticho");
		var aiCredit = newCredit("ai", "Michiel");
		var developersCredit = newCredit("developers", "Michiel, Bas, Stef, Omar, Ticho");
		var moralSupportCredit = newCredit("moral-support", "Wesley");
		var openglCredit = newCredit("opengl", "Omar");

		var topSpacer = new Region();
		topSpacer.setPrefHeight(App.getHeight());

		var bottomSpacer = new Region();
		bottomSpacer.setPrefHeight(App.getHeight());

		var creditsContainer = Primitive.vbox(
			topSpacer,

			scrumMasterCredit,
			productOwnerCredit,
			mergeCommanderCredit,
			localizationCredit,
			aiCredit,
			developersCredit,
			moralSupportCredit,
			openglCredit,

			bottomSpacer
		);

		var creditsScroll = Primitive.scroll(creditsContainer);

		creditsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		creditsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		add(Pos.CENTER, creditsScroll);

		animate(creditsScroll, 15);
	}

	private HBox newCredit(String key, String other) {
		var credit = new Text(": " + other);
		credit.getStyleClass().add("header");

		var creditBox = Primitive.hbox(
			Primitive.header(key),
			credit
		);

		creditBox.setPrefHeight(App.getHeight() / 3.0);
		return creditBox;
	}

	private void animate(ScrollPane scroll, int length) {
		final Timeline timeline = new Timeline(
			new KeyFrame(Duration.seconds(0), new KeyValue(scroll.vvalueProperty(), 0.0)),
			new KeyFrame(Duration.seconds(length), new KeyValue(scroll.vvalueProperty(), 1.0))
		);

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
}