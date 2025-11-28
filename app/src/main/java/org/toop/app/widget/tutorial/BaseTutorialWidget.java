package org.toop.app.widget.tutorial;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.toop.app.widget.Primitive;
import org.toop.app.widget.WidgetContainer;
import org.toop.app.widget.complex.PopupWidget;
import org.toop.app.widget.complex.ViewWidget;

import javafx.scene.control.Button;
import org.toop.local.AppContext;

import java.io.File;

public class BaseTutorialWidget extends PopupWidget {

    private Text tutorialText;
    private Button previousButton;
    private Button nextButton;
    private ImageView imagery;
    private int currentTextIndex = 0;

    public BaseTutorialWidget(String key) {
        System.out.println("Trying to initialize...");
        this.tutorialText = Primitive.text(key);
        WidgetContainer.add(Pos.CENTER, this);
    }

    private void setOnPrevious(Runnable onPrevious) {
        this.previousButton = Primitive.button("<", onPrevious);
    }

    private void setOnNext(Runnable onNext) {
        this.nextButton = Primitive.button(">", onNext);
    }

    public void setTutorial(File image, Runnable onPrevious, Runnable onNext) {
        setOnPrevious(onPrevious);
        setOnNext(onNext);
        this.imagery = Primitive.image(image);
        var w = Primitive.hbox(previousButton, nextButton);
        var x =  Primitive.vbox(imagery, tutorialText);
        add(Pos.CENTER, Primitive.vbox(x, w));
    }

    public void update(boolean next, String[] locKeys, File[] imgs) {
        currentTextIndex = next ? currentTextIndex + 1 : currentTextIndex - 1;

        if (currentTextIndex >= locKeys.length) {
            currentTextIndex--;
            return;
        } else if (currentTextIndex < 0) {
            currentTextIndex++;
            return;
        }

        tutorialText.textProperty().unbind();
        tutorialText.setText(AppContext.getString(locKeys[currentTextIndex]));
        imagery.setImage(Primitive.image(imgs[currentTextIndex]).getImage());
    }
}
