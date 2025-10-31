package org.toop.app.widget.complex;

import javafx.geometry.Pos;
import org.toop.app.widget.Primitive;

public abstract class PrimaryWidget extends StackWidget {
	private PrimaryWidget previous = null;

    public PrimaryWidget() {
        super("bg-primary");
    }

	public void transitionNext(PrimaryWidget primary) {
		primary.previous = this;
		replace(Pos.CENTER, primary);

		var backButton = Primitive.button("back", () -> {
			primary.transitionPrevious();
		});

		primary.add(Pos.BOTTOM_LEFT, Primitive.vbox(backButton));
	}

	public void transitionPrevious() {
		if (previous == null) {
			return;
		}

		replace(Pos.CENTER, previous);
		previous = null;
	}

	public void reload(PrimaryWidget primary) {
		primary.previous = previous;
		replace(Pos.CENTER, primary);

		var backButton = Primitive.button("back", () -> {
			primary.transitionPrevious();
		});

		primary.add(Pos.BOTTOM_LEFT, Primitive.vbox(backButton));
	}
}