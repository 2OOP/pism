package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.complex.ViewWidget;

import javafx.scene.control.Button;
import org.toop.local.AppContext;

import java.io.File;

public class BaseTutorialWidget extends ViewWidget {

    private TState state;
    private Text tutorialText;
    private Button previousButton;
    private Button nextButton;
    private Button noButton;
    private Button yesButton;
    private Button neverButton;
    private ImageView imagery;

    public BaseTutorialWidget(String key, Runnable onNo, Runnable onYes, Runnable onNever) {
        System.out.println("Trying to initialize...");
        this.tutorialText = Primitive.text(key);
        this.yesButton = Primitive.button("ok", () -> onYes.run());
        this.noButton = Primitive.button("no", () -> onNo.run());
        this.neverButton = Primitive.button("never", () -> onNever.run());
        var a = Primitive.hbox(yesButton, noButton, neverButton);
        add(Pos.CENTER, Primitive.vbox(tutorialText, a));
    }

    public BaseTutorialWidget(TState state, String key, Runnable onPrevious, Runnable onNext) {
        this.state = state;
        this.tutorialText = Primitive.text(key);
        this.previousButton = Primitive.button("<", () -> onPrevious.run());
        this.nextButton = Primitive.button(">", () -> onNext.run());
        var w = Primitive.hbox(previousButton, nextButton);
        add(Pos.CENTER, Primitive.vbox(tutorialText, w));
    }

    public BaseTutorialWidget(TState state, String key, File image,  Runnable onPrevious, Runnable onNext) {
        this.state = state;
        this.imagery = Primitive.image(image);
        this.tutorialText = Primitive.text(key);
        this.previousButton = Primitive.button("<", () -> onPrevious.run());
        this.nextButton = Primitive.button(">", () -> onNext.run());
        var w = Primitive.hbox(previousButton, nextButton);
        var x =  Primitive.vbox(imagery, tutorialText);
        add(Pos.CENTER, Primitive.vbox(x, w));
    }

    public void update(String key, File image) {
        tutorialText.textProperty().unbind();
        tutorialText.setText(AppContext.getString(key));
        imagery.setImage(Primitive.image(image).getImage());
    }
}
