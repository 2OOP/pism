package org.toop.events;

import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

public class WindowEvents extends EventsBase {
    /** Triggers when a cell is clicked in one of the game boards. */
    public record CellClicked(int cell) implements EventWithoutSnowflake {}

    /** Triggers when the window wants to quit. */
    public record OnQuitRequested() implements EventWithoutSnowflake {}
}