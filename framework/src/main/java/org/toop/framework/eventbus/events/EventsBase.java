package org.toop.framework.eventbus.events;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/** Events that are used in the GlobalEventBus class. */
public class EventsBase {

    /**
     * WIP, DO NOT USE!
     *
     * @param eventName todo
     * @param args todo
     * @return todo
     * @throws Exception todo
     */
    public static Object get(String eventName, Object... args) throws Exception {
        Class<?> clazz = Class.forName("org.toop.framework.eventbus.events.Events$ServerEvents$" + eventName);
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        Constructor<?> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(args);
    }

    /**
     * WIP, DO NOT USE!
     *
     * @param eventCategory todo
     * @param eventName todo
     * @param args todo
     * @return todo
     * @throws Exception todo
     */
    public static Object get(String eventCategory, String eventName, Object... args)
            throws Exception {
        Class<?> clazz =
                Class.forName("org.toop.framework.eventbus.events.Events$" + eventCategory + "$" + eventName);
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        Constructor<?> constructor = clazz.getConstructor(paramTypes);
        return constructor.newInstance(args);
    }

    /**
     * WIP, DO NOT USE!
     *
     * @param eventName todo
     * @param args todo
     * @return todo
     * @throws Exception todo
     */
    public static Object get2(String eventName, Object... args) throws Exception {
        // Fully qualified class name
        String className = "org.toop.server.backend.Events$ServerEvents$" + eventName;

        // Load the class
        Class<?> clazz = Class.forName(className);

        // Build array of argument types
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }

        // Get the constructor
        Constructor<?> constructor = clazz.getConstructor(paramTypes);

        // Create a new instance
        return constructor.newInstance(args);
    }
}
