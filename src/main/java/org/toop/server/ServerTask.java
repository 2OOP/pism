package org.toop.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.toop.Main;

public class ServerTask extends Thread {
    private static final Logger logger = LogManager.getLogger(Main.class);
    Server server;

    public ServerTask(Server server) {
        this.server = server;
    }

    public void run() {
        while (true) {
//            logger.info(server.getIp());
            logger.info(this.isAlive());
        }
    }

}
