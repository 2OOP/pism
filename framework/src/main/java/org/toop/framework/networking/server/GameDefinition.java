package org.toop.framework.networking.server;

import java.lang.reflect.InvocationTargetException;

public class GameDefinition<T> {
    private final String name;
    private final Class<T> game;

    public GameDefinition(String name, Class<T> game) {
        this.name = name;
        this.game = game;
    }

    public String name() {
        return name;
    }

    public T create(String... users) {
        try {
            return game.getDeclaredConstructor().newInstance(users);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
