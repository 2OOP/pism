package org.toop.framework.networking.server.stores;

import org.toop.framework.utils.ImmutablePair;

import java.util.*;

public class MapSubscriptionStore implements SubscriptionStore {

    final private Map<String, List<String>> subscriptions;

    public MapSubscriptionStore(Map<String, List<String>> initMap) {
        this.subscriptions = initMap;
    }

    @Override
    public void add(ImmutablePair<String, String> adding) {
        subscriptions.forEach((_, clientNames) -> clientNames.remove(adding.getRight()));
        subscriptions.computeIfAbsent(
                        adding.getLeft(),
                        _ -> new ArrayList<>())
                .add(adding.getRight());
    }

    @Override
    public void remove(String remover) {
        subscriptions.forEach((_, clientNames) -> clientNames.remove(remover));
    }

    // TODO move server internal code to here
    @Override
    public ImmutablePair<String, String> get(String getter) {
        String foundKey = null;
        String foundName = null;

        for (var key : subscriptions.keySet()) {
            var a = subscriptions.get(key).stream().filter(e -> e.equals(getter)).toList();
            if (!a.isEmpty()) {
                foundKey = key;
                foundName = a.getFirst();
                break;
            }
        }

        return new ImmutablePair<>(foundKey, foundName);

    }

    @Override
    public Collection<ImmutablePair<String, String>> all() {
        List<ImmutablePair<String, String>> a = new ArrayList<>();

        for (var key : subscriptions.keySet()) {
            for (var sub : subscriptions.get(key)) {
                a.addLast(new ImmutablePair<>(key, sub));
            }
        }

        return a;

    }

    @Override
    public Collection<String> allKeys() {
        return subscriptions.keySet();
    }

    @Override
    public Collection<String> allValues(String key) {
        return subscriptions.get(key);
    }
}
