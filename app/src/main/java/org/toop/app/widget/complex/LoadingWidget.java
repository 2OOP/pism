package org.toop.app.widget.complex;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.toop.app.widget.Primitive;

public class LoadingWidget extends ViewWidget implements Update { // TODO make of widget type
    private final ProgressBar progressBar;
    private final Text loadingText;

    private Runnable success = () -> {};
    private Runnable failure = () -> {};
    private boolean successTriggered = false;
    private boolean failureTriggered = false;
    private int maxAmount;
    private int amount;
    private float percentage = 0.0f;


    public LoadingWidget(Text loadingText, int startAmount, int maxAmount) {

        amount = startAmount;
        this.maxAmount = maxAmount;

        progressBar = new ProgressBar();
        this.loadingText = loadingText;

        VBox box = Primitive.vbox(this.loadingText, progressBar);
        add(Pos.CENTER, box);
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        update();
    }

    public void setAmount() {
        setAmount(this.amount+1);
    }

    public void setOnSuccess(Runnable onSuccess) {
        success = onSuccess;
    }

    public void setOnFailure(Runnable onFailure) {
        failure = onFailure;
    }

    public void triggerSuccess() {
        successTriggered = true; // TODO, else it will double call... why?
        success.run();
    }

    public void triggerFailure() {
        failureTriggered = true; // TODO, else it will double call... why?
        failure.run();
    }

    @Override
    public void update() {
        if (successTriggered || failureTriggered) {
            return;
        }

        if (amount >= maxAmount) {
            triggerSuccess();
            this.remove(this);
            return;
        } else if (amount < 0) {
            triggerFailure();
            this.remove(this);
            return;
        }

        percentage = (float) amount / maxAmount;
        progressBar.setProgress(percentage);

    }
}
