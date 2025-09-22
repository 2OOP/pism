//package org.toop.frontend.graphics;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.toop.frontend.platform.graphics.opengl.OpenglRenderer;
//
//public abstract class Renderer {
//    public enum API {
//        NONE,
//        OPENGL,
//    };
//
//    protected static final Logger logger = LogManager.getLogger(Renderer.class);
//
//    private static API api = API.NONE;
//    private static Renderer instance = null;
//
//    public static Renderer setup(API api) {
//        if (instance != null) {
//            logger.warn("Renderer is already setup.");
//            return instance;
//        }
//
//        switch (api) {
//            case OPENGL:
//                instance = new OpenglRenderer();
//                break;
//
//            default:
//                logger.fatal("No valid renderer api chosen");
//                return null;
//        }
//
//        Renderer.api = api;
//        return instance;
//    }
//
//    public static API getApi() {
//        return api;
//    }
//
//    public void cleanup() {
//        instance = null;
//        logger.info("Renderer cleanup.");
//    }
//
//    public abstract void clear();
//
//    public abstract void render();
//}
