package org.toop;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.Server;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        if (!initEvents()) {
            throw new RuntimeException("A event could not be initialized");
        }

        Server.start("local", "127.0.0.1", "5001");
        Window.start();

    }

    /**
     * Returns false if any event could not be initialized.
     */
     private static boolean initEvents() {
        try {
            GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnChangingServerBackend.class, e ->
                    logger.info("Changing server backend state to {}", e.backend())
            );

            GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnChangingServerIp.class, e ->
                    logger.info("Changing server ip to {}", e.ip())
            );

            GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnChangingServerPort.class, e ->
                    logger.info("Changing server port to {}", e.port())
            );

            return true;
        }
        catch (Exception e) {
            logger.info("{}", e.getMessage());
            return false;
        }
     }

}