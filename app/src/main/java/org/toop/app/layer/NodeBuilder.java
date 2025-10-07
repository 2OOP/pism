package org.toop.app.layer;

import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.toop.framework.audio.events.AudioEvents;
import org.toop.framework.eventbus.EventFlow;

public final class NodeBuilder {
    public static void addCss(Node node, String... cssClasses) {
        node.getStyleClass().addAll(cssClasses);
    }

    public static void setCss(Node node, String... cssClasses) {
        node.getStyleClass().removeAll();
        node.getStyleClass().addAll(cssClasses);
    }

    public static Text header(String x) {
        final Text element = new Text(x);
        setCss(element, "text-primary", "text-header");

        return element;
    }

    public static Text text(String x) {
        final Text element = new Text(x);
        setCss(element, "text-secondary", "text-normal");

        return element;
    }

    public static Label button(String x, Runnable runnable) {
        final Label element = new Label(x);
        setCss(element, "button", "text-normal");

        element.setOnMouseClicked(
                _ -> {
                    new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
                    runnable.run();
                });

        return element;
    }

    public static Label toggle(String x1, String x2, boolean toggled, Consumer<Boolean> consumer) {
        final Label element = new Label(toggled ? x2 : x1);
        setCss(element, "toggle", "text-normal");

        final BooleanProperty checked = new SimpleBooleanProperty(toggled);

        element.setOnMouseClicked(
                _ -> {
                    new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
                    checked.set(!checked.get());

                    if (checked.get()) {
                        element.setText(x1);
                    } else {
                        element.setText(x2);
                    }

                    consumer.accept(checked.get());
                });

        return element;
    }

    public static Slider slider(int max, int initial, Consumer<Integer> consumer) {
        final Slider element = new Slider(0, max, initial);
        setCss(element, "bg-slider-track");

        element.setMinorTickCount(0);
        element.setMajorTickUnit(1);
        element.setBlockIncrement(1);

        element.setSnapToTicks(true);
        element.setShowTickLabels(true);

        element.setOnMouseClicked(
                _ -> {
                    new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
                });

        element.valueProperty()
                .addListener(
                        (_, _, newValue) -> {
                            consumer.accept(newValue.intValue());
                        });

        return element;
    }

    public static TextField input(String x, Consumer<String> consumer) {
        final TextField element = new TextField(x);
        setCss(element, "input", "text-normal");

        element.setOnMouseClicked(
                _ -> {
                    new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
                });

        element.textProperty()
                .addListener(
                        (_, _, newValue) -> {
                            consumer.accept(newValue);
                        });

        return element;
    }

    public static <T> ChoiceBox<T> choiceBox(Consumer<T> consumer) {
        final ChoiceBox<T> element = new ChoiceBox<>();
        setCss(element, "choice-box", "text-normal");

        element.setOnMouseClicked(
                _ -> {
                    new EventFlow().addPostEvent(new AudioEvents.ClickButton()).asyncPostEvent();
                });

        element.valueProperty()
                .addListener(
                        (_, _, newValue) -> {
                            consumer.accept(newValue);
                        });

        return element;
    }

    public static Separator separator() {
        final Separator element = new Separator(Orientation.HORIZONTAL);
        setCss(element, "separator");

        return element;
    }
}
