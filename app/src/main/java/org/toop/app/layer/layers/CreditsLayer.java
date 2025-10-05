package org.toop.app.layer.layers;

import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.Layer;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.local.AppContext;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.util.Duration;

public final class CreditsLayer extends Layer {
	private final int lineHeight = 100;

	CreditsLayer() {
		super("credits.css");
		reload();
	}

	@Override
	public void reload() {
		popAll();

		final String[] credits = {
				AppContext.getString("scrumMaster") + ": Stef",
				AppContext.getString("productOwner") + ": Omar",
				AppContext.getString("mergeCommander") + ": Bas",
				AppContext.getString("localization") + ": Ticho",
				AppContext.getString("ai") + ": Michiel",
				AppContext.getString("developers") + ": Michiel, Bas, Stef, Omar, Ticho",
				AppContext.getString("moralSupport") + ": Wesley",
				AppContext.getString("opengl") + ": Omar"
		};

		final Container creditsContainer = new HorizontalContainer(0);

		final Container animatedContainer = new VerticalContainer("animated_credits_container", lineHeight);
		creditsContainer.addContainer(animatedContainer, true);

		for (final String credit : credits) {
			animatedContainer.addText("credit-text", credit, false);
		}

		final Container controlContainer = new VerticalContainer(5);
		controlContainer.addButton(AppContext.getString("back"), () -> {
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