package org.toop.framework.gui;

import org.toop.framework.eventbus.events.EventsBase;
import org.toop.framework.eventbus.events.GenericEvent;

public class GUIEvents extends EventsBase{
    public record UpdateGameCanvas() implements GenericEvent{}

    public record PlayerAttemptedMove(int move) implements GenericEvent{}
}
