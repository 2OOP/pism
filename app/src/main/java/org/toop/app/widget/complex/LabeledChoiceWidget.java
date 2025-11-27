package org.toop.app.widget.complex;

import org.toop.app.widget.Primitive;
import org.toop.app.widget.Widget;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class LabeledChoiceWidget<T> implements Widget {
	private final ComboBox<T> comboBox;
	private final VBox container;

	@SafeVarargs
	public LabeledChoiceWidget(
		String key,
		StringConverter<T> converter,
		T initialValue,
		Consumer<T> onValueChanged,
		T... items
	) {
		var label = Primitive.text(key);
		comboBox = Primitive.choice(converter, initialValue, onValueChanged, items);
		container = Primitive.vbox(label, comboBox);
	}

	public T getValue() {
		return comboBox.getValue();
	}

	public void setValue(T value) {
		comboBox.setValue(value);
	}

	@Override
	public Node getNode() {
		return container;
	}
}