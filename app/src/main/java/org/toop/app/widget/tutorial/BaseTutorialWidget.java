package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.apache.maven.surefire.shared.lang3.tuple.ImmutablePair;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.Updatable;
import org.toop.app.widget.complex.PopupWidget;

import org.toop.framework.resource.resources.ImageAsset;
import org.toop.local.AppContext;

import java.util.List;

/**
 * A widget base for all the tutorial widgets.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * public class Connect4TutorialWidget extends BaseTutorialWidget {
 *     public Connect4TutorialWidget(Runnable nextScreen) {
 *         super(List.of(
 *                 new ImmutablePair<>("connect4.1", ResourceManager.get("connect41.png")),
 *                 new ImmutablePair<>("connect4.2", ResourceManager.get("connect42.png"))
 *         ), nextScreen);
 *     }
 * }</pre>
 */
public class BaseTutorialWidget extends PopupWidget implements Updatable {

    private final Text tutorialText;
    private final ImageView imagery;
    private final Button previousButton;
    private final Button nextButton;
    private final List<ImmutablePair<String, ImageAsset>> pages;
    private final Runnable nextScreen;

    private int pageIndex = 0;

    public BaseTutorialWidget(List<ImmutablePair<String, ImageAsset>> pages, Runnable nextScreen) {
        this.tutorialText = Primitive.text(pages.getFirst().getKey());
        this.imagery = Primitive.image(pages.getFirst().getValue());

        this.pages = pages;
        this.nextScreen = nextScreen;

        previousButton = Primitive.button("goback", () -> { update(false); this.hide(); }, false);
        nextButton = Primitive.button(">", () -> update(true), false);

        var w = Primitive.hbox(
                previousButton,
                nextButton
        );

        var x = Primitive.vbox(imagery, tutorialText);

        add(Pos.CENTER, Primitive.vbox(x, w));
    }

    @Override
    public void update() {
        update(true);
    }

    // TODO Refactor if statements to make code easier to read.
    public void update(boolean next) {
        pageIndex = next ? pageIndex + 1 : pageIndex - 1;

        if (pageIndex >= pages.size()) {
            pageIndex--;
            return;
        } else if (pageIndex < 0) {
            pageIndex++;
            return;
        }

        if (pageIndex == pages.size()-1) {
            nextButton.textProperty().unbind();
            nextButton.setText(AppContext.getString("startgame"));
            nextButton.setOnAction((_) ->  {
                this.hide();
                nextScreen.run();
            });
        } else {
            nextButton.textProperty().unbind();
            nextButton.setText(AppContext.getString(">"));
            nextButton.setOnAction((_) -> this.update(true));
        }

        if (pageIndex == 0) {
            previousButton.textProperty().unbind();
            previousButton.setText(AppContext.getString("goback"));
            previousButton.setOnAction((_) -> this.hide());
        } else {
            previousButton.textProperty().unbind();
            previousButton.setText(AppContext.getString("<"));
            previousButton.setOnAction((_) -> this.update(false));
        }

        var currentPage = pages.get(pageIndex);

        var text = currentPage.getKey();
        var image = currentPage.getValue();

        tutorialText.textProperty().unbind();
        tutorialText.setText(AppContext.getString(text));
        imagery.setImage(Primitive.image(image).getImage());
    }
}
