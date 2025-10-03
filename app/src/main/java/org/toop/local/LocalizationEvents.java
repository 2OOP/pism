package org.toop.local;


import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

public class LocalizationEvents extends EventsBase {
    public record LanguageHasChanged(String language) implements EventWithoutSnowflake {}
}
