package org.toop.app.widget.complex;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class LoadingWidget extends ViewWidget implements Update { // TODO make of widget type
    private final ProgressBar progressBar;

    private Runnable success = () -> {};
    private Runnable failure = () -> {};
    private int maxAmount;
    private int amount;
    private float percentage = 0.0f;


    public LoadingWidget(int startAmount, int maxAmount) {

        amount = startAmount;
        this.maxAmount = maxAmount;

        progressBar = new ProgressBar();

        HBox box = new HBox(10, progressBar);
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
        success.run();
    }

    public void triggerFailure() {
        failure.run();
    }

    @Override
    public void update() {
        if (amount >= maxAmount) {
            triggerSuccess();
            System.out.println("triggered");
            this.hide();
            return;
        } else if (amount < 0) {
            triggerFailure();
            System.out.println("triggerFailure");
            this.hide();
            return;
        }

        percentage = (float) amount / maxAmount;
        progressBar.setProgress(percentage);

    }
}
