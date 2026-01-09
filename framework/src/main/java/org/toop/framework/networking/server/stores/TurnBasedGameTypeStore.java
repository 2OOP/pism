package org.toop.framework.networking.server.stores;

import org.toop.framework.gameFramework.model.game.TurnBasedGame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class TurnBasedGameTypeStore {

    private final Map<String, Supplier<? extends TurnBasedGame>> gameFactories = new ConcurrentHashMap<>();

    public TurnBasedGameTypeStore() {}

    public void register(String key, Supplier<? extends TurnBasedGame> factory) {
        gameFactories.put(key, factory);
    }

    public void unregister(String key) {
        gameFactories.remove(key);
    }

    public TurnBasedGame create(String key) {
        Supplier<? extends TurnBasedGame> factory = gameFactories.get(key);
        if (factory == null) throw new IllegalArgumentException("Unknown game type: " + key);
        return factory.get();
    }

    public Map<String, Supplier<? extends TurnBasedGame>> all() {
        return Map.copyOf(gameFactories);
    }
}