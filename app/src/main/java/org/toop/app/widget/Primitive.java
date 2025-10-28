package org.toop.app.widget;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public final class Primitive {
    public static Text header(String label) {
        var header = new Text(label);
        header.getStyleClass().add("header");
        return header;
    }

    public static Text text(String label) {
        var text = new Text(label);
        text.getStyleClass().add("text");
        return text;
    }

    public static Button button(String label) {
        var button = new Button(label);
        button.getStyleClass().add("button");
        return button;
    }

    public static TextField input() {
        var input = new TextField();
        input.getStyleClass().add("input");
        return input;
    }

    public static Slider slider() {
        var slider = new Slider();
        slider.getStyleClass().add("slider");
        return slider;
    }

    public static <T> ComboBox<T> choice() {
        var choice = new ComboBox<T>();
        choice.getStyleClass().add("choice");
        return choice;
    }

    public static ScrollPane scroll(Node content) {
        var scroll = new ScrollPane(content);
        scroll.getStyleClass().add("scroll");
        return scroll;
    }

    public static HBox hbox(Node... nodes) {
        var hbox = new HBox(nodes);
        hbox.getStyleClass().add("container");
        return hbox;
    }

    public static VBox vbox(Node... nodes) {
        var vbox = new VBox(nodes);
        vbox.getStyleClass().add("container");
        return vbox;
    }
}