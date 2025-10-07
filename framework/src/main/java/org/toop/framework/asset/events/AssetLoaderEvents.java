package org.toop.framework.asset.events;

import org.toop.framework.eventbus.events.EventWithoutSnowflake;

public class AssetLoaderEvents {
    public record LoadingProgressUpdate(int hasLoadedAmount, int isLoadingAmount)
            implements EventWithoutSnowflake {}
}
