package org.toop.events;

import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

public class WindowEvents extends EventsBase {

    /** Triggers when a cell is clicked in one of the game boards. */
    public record CellClicked(int cell) implements EventWithoutSnowflake {}

    /** Triggers when the window wants to quit. */
    public record OnQuitRequested() implements EventWithoutSnowflake {}

    /** Triggers when the window is resized. */
//        public record OnResize(Window.Size size) implements EventWithoutSnowflake {}

    /** Triggers when the mouse is moved within the window. */
    public record OnMouseMove(int x, int y) implements EventWithoutSnowflake {}

    /** Triggers when the mouse is clicked within the window. */
    public record OnMouseClick(int button) implements EventWithoutSnowflake {}

    /** Triggers when the mouse is released within the window. */
    public record OnMouseRelease(int button) implements EventWithoutSnowflake {}
}