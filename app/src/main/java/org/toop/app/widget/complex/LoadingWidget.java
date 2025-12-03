package org.toop.app.widget.complex;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.toop.app.widget.Primitive;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadingWidget extends ViewWidget implements Update { // TODO make of widget type
    private final Text loadingText; // TODO Make changeable
    private final ProgressBar progressBar;
    private final AtomicBoolean successTriggered = new AtomicBoolean(false);
    private final AtomicBoolean failureTriggered = new AtomicBoolean(false);

    private Runnable success = () -> {};
    private Runnable failure = () -> {};
    private int maxAmount;
    private int minAmount;
    private int amount;
    private Callable<Boolean> successTrigger = () -> (amount >= maxAmount);
    private Callable<Boolean> failureTrigger = () -> (amount < minAmount);
    private float percentage = 0.0f;

    private boolean isInfiniteBar = false;

    /**
     *
     * Widget that shows a loading bar.
     *
     * @param loadingText Text above the loading bar.
     * @param minAmount The minimum amount.
     * @param startAmount The starting amount.
     * @param maxAmount The max amount.
     */
    public LoadingWidget(Text loadingText, int minAmount, int startAmount, int maxAmount, boolean infiniteBar) {
        isInfiniteBar = infiniteBar;

        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        amount = startAmount;

        this.loadingText = loadingText;
        progressBar = new ProgressBar();

        VBox box = Primitive.vbox(this.loadingText, progressBar);
        add(Pos.CENTER, box);
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setAmount(int amount) throws Exception {
        this.amount = amount;
        update();
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getAmount() {
        return amount;
    }

    public float getPercentage() {
        return percentage;
    }

    public boolean isTriggered() {
        return (failureTriggered.get() || successTriggered.get());
    }

    /**
     * What to do when success is triggered.
     * @param onSuccess The lambda that gets run on success.
     */
    public void setOnSuccess(Runnable onSuccess) {
        success = onSuccess;
    }

    /**
     * What to do when failure is triggered.
     * @param onFailure The lambda that gets run on failure.
     */
    public void setOnFailure(Runnable onFailure) {
        failure = onFailure;
    }

    /**
     * The trigger to activate onSuccess.
     * @param trigger The lambda that triggers onSuccess.
     */
    public void setSuccessTrigger(Callable<Boolean> trigger) {
        successTrigger = trigger;
    }

    /**
     * The trigger to activate onFailure.
     * @param trigger The lambda that triggers onFailure.
     */
    public void setFailureTrigger(Callable<Boolean> trigger) {
        failureTrigger = trigger;
    }

    /**
     * Forcefully trigger success.
     */
    public void triggerSuccess() {
		if (successTriggered.compareAndSet(false, true)) {
			Platform.runLater(() -> {
				if (success != null) success.run();
			});
		}
    }

    /**
     * Forcefully trigger failure.
     */
    public void triggerFailure() {
		if (failureTriggered.compareAndSet(false, true)) {
			Platform.runLater(() -> {
				if (failure != null) failure.run();
			});
		}
    }

    @Override
    public void update() throws Exception { // TODO Better exception
        if (successTriggered.get() || failureTriggered.get()) { // If already triggered, throw exception.
            throw new RuntimeException();
        }

        if (successTrigger.call()) {
            triggerSuccess();
            this.remove(this);
            return;
        } else if (failureTrigger.call()) {
            triggerFailure();
            this.remove(this);
            return;
        }

        if (maxAmount != 0) {
            percentage = (float) amount / maxAmount;
        }
        if (!isInfiniteBar) {
            progressBar.setProgress(percentage);
        }
    }
}
