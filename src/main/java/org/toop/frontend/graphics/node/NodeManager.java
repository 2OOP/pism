//package org.toop.frontend.graphics.node;
//
//import java.util.*;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.toop.eventbus.*;
//import org.toop.eventbus.events.Events;
//import org.toop.frontend.graphics.Shader;
//
//public class NodeManager {
//    private static final Logger logger = LogManager.getLogger(NodeManager.class);
//
//    private static NodeManager instance = null;
//
//    public static NodeManager setup() {
//        if (instance != null) {
//            logger.warn("NodeManager is already setup.");
//            return instance;
//        }
//
//        instance = new NodeManager();
//        return instance;
//    }
//
//    private Shader shader;
//    private ArrayList<Node> nodes;
//    private Node active;
//
//    private NodeManager() {
//        shader =
//                Shader.create(
//                        "src/main/resources/shaders/gui_vertex.glsl",
//                        "src/main/resources/shaders/gui_fragment.glsl");
//
//        nodes = new ArrayList<Node>();
//
//        GlobalEventBus.subscribeAndRegister(
//                Events.WindowEvents.OnMouseMove.class,
//                event -> {
//                    for (int i = 0; i < nodes.size(); i++) {
//                        Node node = nodes.get(i);
//
//                        if (node.check(event.x(), event.y())) {
//                            active = node;
//                            node.hover();
//
//                            break;
//                        }
//                    }
//                });
//
//        GlobalEventBus.subscribeAndRegister(
//                Events.WindowEvents.OnMouseClick.class,
//                event -> {
//                    if (active != null) {
//                        active.click();
//                    }
//                });
//    }
//
//    public void cleanup() {}
//
//    public void add(Node node) {
//        nodes.add(node);
//    }
//
//    public void render() {}
//}
