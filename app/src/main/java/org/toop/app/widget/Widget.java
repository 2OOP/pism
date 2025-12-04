package org.toop.app.widget;

import javafx.geometry.Pos;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Widget {
    Logger logger = LogManager.getLogger(Widget.class);

	Node getNode();

    default void show(Pos position) {
        logger.debug("Showing Widget: {} at position: {}", this.getNode(), position.toString());
        WidgetContainer.add(position, this);
    }

    default void hide() {
        logger.debug("Hiding Widget: {}", this.getNode());
        WidgetContainer.remove(this);
    }

    default void replace(Pos position, Widget widget) {
        logger.debug("Replacing Widget: {}, with widget: {}, to position: {}",
                this.getNode(), widget.getNode(), position.toString());
        widget.show(position);
		hide();
    }
}