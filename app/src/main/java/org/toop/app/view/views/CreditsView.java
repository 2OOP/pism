package org.toop.app.view.views;

import org.toop.app.App;
import org.toop.app.view.View;
import org.toop.app.view.ViewStack;
import org.toop.app.view.displays.SongDisplay;
import org.toop.local.AppContext;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

public final class CreditsView extends View {
	public CreditsView() {
		super(false, "bg-primary");
	}

	@Override
	public void setup() {
		final Text scrumMasterHeader = header();
		scrumMasterHeader.setText(AppContext.getString("scrum-master") + ": Stef");

		final Text productOwnerHeader = header();
		productOwnerHeader.setText(AppContext.getString("product-owner") + ": Omar");

		final Text mergeCommanderHeader = header();
		mergeCommanderHeader.setText(AppContext.getString("merge-commander") + ": Bas");

		final Text localizationHeader = header();
		localizationHeader.setText(AppContext.getString("localization") + ": Ticho");

		final Text aiHeader = header();
		aiHeader.setText(AppContext.getString("ai") + ": Michiel");

		final Text developersHeader = header();
		developersHeader.setText(AppContext.getString("developers") + ": Michiel, Bas, Stef, Omar, Ticho");

		final Text moralSupportHeader = header();
		moralSupportHeader.setText(AppContext.getString("moral-support") + ": Wesley");

		final Text openglHeader = header();
		openglHeader.setText(AppContext.getString("opengl") + ": Omar");

        final SongDisplay songdisplay = new SongDisplay();


        add(Pos.BOTTOM_RIGHT,
                fit(vboxFill(
                        songdisplay
                )));

		add(Pos.CENTER,
			fit("credits-fit", vboxFill("credits-container", "credits-container",
				vbox("credits-spacer-top", ""),

				scrumMasterHeader,
				productOwnerHeader,
				mergeCommanderHeader,
				localizationHeader,
				aiHeader,
				developersHeader,
				moralSupportHeader,
				openglHeader,

				vbox("credits-spacer-bottom", "")
			))
		);

		final Button backButton = button();
		backButton.setText(AppContext.getString("back"));
		backButton.setOnAction(_ -> { ViewStack.pop(); });

		add(Pos.BOTTOM_LEFT,
			vboxFill(
				backButton
			)
		);

		playCredits(100, 20);
	}

	private void playCredits(int lineHeight, int length) {
		final ScrollPane creditsFit = get("credits-fit");
		creditsFit.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		creditsFit.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		final VBox creditsContainer = get("credits-container");
		creditsContainer.setSpacing(lineHeight);

		final VBox creditsSpacerTop = get("credits-spacer-top");
		creditsSpacerTop.setMinHeight(App.getHeight() - lineHeight);

		final VBox creditsSpacerBottom = get("credits-spacer-bottom");
		creditsSpacerBottom.setMinHeight(App.getHeight() - lineHeight);

		final Timeline timeline = new Timeline(
			new KeyFrame(Duration.seconds(0), new KeyValue(creditsFit.vvalueProperty(), 0.0)),
			new KeyFrame(Duration.seconds(length), new KeyValue(creditsFit.vvalueProperty(), 1.0))
		);

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}
}