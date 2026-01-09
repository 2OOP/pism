package org.toop.framework.networking.server.stores;

import org.toop.framework.utils.ImmutablePair;

import java.util.Collection;

public interface SubscriptionStore extends Store<String, ImmutablePair<String, String>> {
    Collection<String> allKeys();
    Collection<String> allValues(String key);
}
