package org.toop.app.layer.layers;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.toop.app.App;
import org.toop.app.layer.Container;
import org.toop.app.layer.NodeBuilder;
import org.toop.app.layer.Popup;
import org.toop.app.layer.containers.HorizontalContainer;
import org.toop.app.layer.containers.VerticalContainer;
import org.toop.local.AppContext;

public final class CreditsPopup extends Popup {
    private final int lineHeight = 100;

    public CreditsPopup() {
        super(true, "bg-primary");
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

        final Text[] creditsHeaders = new Text[credits.length];

        for (int i = 0; i < credits.length; i++) {
            creditsHeaders[i] = NodeBuilder.header(credits[i]);
        }

        final Container creditsContainer = new HorizontalContainer(0);

        final Container animatedContainer = new VerticalContainer(lineHeight);
        creditsContainer.addContainer(animatedContainer, true);

        animatedContainer.addNodes(creditsHeaders);
        addContainer(creditsContainer, Pos.CENTER, 0, 0, 50, 100);

        playCredits(animatedContainer, App.getHeight());
    }

    private void playCredits(Container container, double sceneLength) {
        container.getContainer().setTranslateY(-sceneLength);

        final TranslateTransition scrollCredits =
                new TranslateTransition(Duration.seconds(20), container.getContainer());
        scrollCredits.setFromY(-sceneLength - lineHeight);
        scrollCredits.setToY(sceneLength + lineHeight);

        scrollCredits.setOnFinished(
                _ -> {
                    final PauseTransition pauseCredits = new PauseTransition(Duration.seconds(3));
                    pauseCredits.setOnFinished(_ -> playCredits(container, sceneLength));
                    pauseCredits.play();
                });

        scrollCredits.play();
    }
}
