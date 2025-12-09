package org.toop.framework.resource.events;

import org.toop.framework.eventbus.events.GenericEvent;

public class AssetLoaderEvents {
    public record LoadingProgressUpdate(int hasLoadedAmount, int isLoadingAmount)
            implements GenericEvent {}
}
