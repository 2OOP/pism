package org.toop.app.widget.complex;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LabeledSliderWidget implements Widget {
	private final Slider slider;
	private final Text labelValue;
	private final VBox container;

	public LabeledSliderWidget(String key, int min, int max, int value, Consumer<Integer> onValueChanged) {
		var label = Primitive.text(key);

		labelValue = new Text(String.valueOf(value));
		labelValue.getStyleClass().add("text");

		slider = Primitive.slider(min, max, value, newValue -> {
			labelValue.setText(String.valueOf(newValue));

			if (onValueChanged != null) {
				onValueChanged.accept(newValue);
			}
		});

		var sliderRow = Primitive.hbox(slider, labelValue);
		container = Primitive.vbox(label, sliderRow);
	}

	public int getValue() {
		return (int)slider.getValue();
	}

	public void setValue(int newValue) {
		slider.setValue(newValue);
		labelValue.setText(String.valueOf(newValue));
	}

	@Override
	public Node getNode() {
		return container;
	}
}