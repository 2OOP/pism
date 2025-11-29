package org.toop.app;

import org.toop.framework.eventbus.events.EventsBase;
import org.toop.framework.eventbus.events.GenericEvent;

public class GUIEvents extends EventsBase{
    public record UpdateGameCanvas() implements GenericEvent{}
}
