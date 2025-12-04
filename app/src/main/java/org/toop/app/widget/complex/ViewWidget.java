package org.toop.app.widget.complex;

import org.toop.app.widget.Primitive;

import javafx.geometry.Pos;

public abstract class ViewWidget extends StackWidget {
	private ViewWidget previous = null;

    public ViewWidget() {
        super("bg-primary");
    }

	public void transition(ViewWidget view) {
		view.previous = this;
		replace(Pos.CENTER, view);
	}

    public void transitionNext(ViewWidget view) {
        transitionNext(view, false);
    }

	public void transitionNext(ViewWidget view, boolean aware) {
        if (aware && this.getClass().equals(view.getClass())) {
            view.previous = this.previous;
        }
        else{
            view.previous = this;
        }

		replace(Pos.CENTER, view);

		var backButton = Primitive.button("back", () -> {
			view.transitionPrevious();
		});

		view.add(Pos.BOTTOM_LEFT, Primitive.vbox(backButton));
	}

	public void transitionNextCustom(ViewWidget view, String key, Runnable runnable) {
		view.previous = this;

		replace(Pos.CENTER, view);

		var customButton = Primitive.button(key, () -> {
			runnable.run();
			view.transitionPrevious();
		});

		view.add(Pos.BOTTOM_LEFT, Primitive.vbox(customButton));
	}

	public void transitionPrevious() {
		if (previous == null) {
			return;
		}

		replace(Pos.CENTER, previous);
		previous = null;
	}

	public void removeIndexFromPreviousChain(int index) {
		ViewWidget view = previous;

		while (index > 0 && view != null) {
			index--;

			if (index == 0) {
				if (view.previous != null && view.previous.previous != null) {
					view.previous = view.previous.previous;
				}
			}

			view = view.previous;
		}
	}

	public void removeViewFromPreviousChain(ViewWidget view) {
		int index = 0;

		while (previous != null) {
			index++;

			if (previous == view) {
				removeIndexFromPreviousChain(index);
				break;
			}
		}
	}

	public void reload(ViewWidget view) {
		view.previous = previous;
		replace(Pos.CENTER, view);

		var backButton = Primitive.button("back", () -> {
			view.transitionPrevious();
		});

		view.add(Pos.BOTTOM_LEFT, Primitive.vbox(backButton));
	}
}