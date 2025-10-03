package org.toop.app.events;

import org.toop.framework.eventbus.events.EventWithoutSnowflake;
import org.toop.framework.eventbus.events.EventsBase;

public class AppEvents extends EventsBase {
	public record OnNodeHover() implements EventWithoutSnowflake {}

	public record OnNodeClick() implements EventWithoutSnowflake {}

	public record OnLanguageChange(String language) implements EventWithoutSnowflake {}
}