package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.util.Duration;

public final class CreditsLayer extends Layer {
	private final int lineHeight = 100;

	private final String[] credits = {
			"Scrum Master: Stef",
			"Product Owner: Omar",
			"Merge Commander: Bas",
			"Localization: Ticho",
			"AI: Michiel",
			"Developers: Michiel, Bas, Stef, Omar, Ticho",
			"Moral Support: Wesley (voor 1 week)",
			"OpenGL: Omar"
	};

	CreditsLayer() {
		super("credits.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final Container creditsContainer = new HorizontalContainer(0);

		final Container animatedContainer = new VerticalContainer("animated_credits_container", lineHeight);
		creditsContainer.addContainer(animatedContainer, true);

		for (final String credit : credits) {
			animatedContainer.addText("credit-text", credit, false);
		}

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addButton("Back", () -> {
			App.activate(new MainLayer());
		});

		addContainer(creditsContainer, Pos.CENTER, 0, 0, 50, 100);
		addContainer(controlContainer, Pos.BOTTOM_LEFT, 2, -2, 0, 0);

		playCredits(animatedContainer, App.getHeight());
	}

	private void playCredits(Container container, double sceneLength) {
		container.getContainer().setTranslateY(-sceneLength);

		final TranslateTransition scrollCredits = new TranslateTransition(Duration.seconds(20), container.getContainer());
		scrollCredits.setFromY(-sceneLength - lineHeight);
		scrollCredits.setToY(sceneLength + lineHeight);

		scrollCredits.setOnFinished(_ -> {
			final PauseTransition pauseCredits = new PauseTransition(Duration.seconds(3));
			pauseCredits.setOnFinished(_ -> playCredits(container, sceneLength));
			pauseCredits.play();
		});

		scrollCredits.play();
	}
}