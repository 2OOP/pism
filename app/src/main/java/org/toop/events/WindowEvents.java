package org.toop.events;

import org.toop.framework.eventbus.events.EventWithoutUuid;
import org.toop.framework.eventbus.events.Events;

public class WindowEvents extends Events {

    /** Triggers when a cell is clicked in one of the game boards. */
    public record CellClicked(int cell) implements EventWithoutUuid {}

    /** Triggers when the window wants to quit. */
    public record OnQuitRequested() implements EventWithoutUuid {}

    /** Triggers when the window is resized. */
//        public record OnResize(Window.Size size) implements EventWithoutUuid {}

    /** Triggers when the mouse is moved within the window. */
    public record OnMouseMove(int x, int y) implements EventWithoutUuid {}

    /** Triggers when the mouse is clicked within the window. */
    public record OnMouseClick(int button) implements EventWithoutUuid {}

    /** Triggers when the mouse is released within the window. */
    public record OnMouseRelease(int button) implements EventWithoutUuid {}
}