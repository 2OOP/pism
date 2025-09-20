package org.toop.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.frontend.platform.core.glfw.GlfwWindow;

public abstract class Window {
    public enum API {
        NONE,
        GLFW,
    }

    public record Size(int width, int height) {}

    protected static final Logger logger = LogManager.getLogger(Window.class);

    private static API api = API.NONE;
    private static Window instance = null;

    public static Window setup(API api, String title, Size size) {
        if (instance != null) {
            logger.warn("Window is already setup.");
            return instance;
        }

        switch (api) {
            case GLFW:
                instance = new GlfwWindow(title, size);
                break;

            default:
                logger.fatal("No valid window api chosen");
                return null;
        }

        Window.api = api;
        return instance;
    }

    public static API getApi() {
        return api;
    }

    public void cleanup() {
        instance = null;
        logger.info("Window cleanup.");
    }

    public abstract void update();
}
