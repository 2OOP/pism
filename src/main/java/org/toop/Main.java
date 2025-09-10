package org.toop;

import org.toop.server.Server;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(Server.ServerBackend.LOCAL, "127.0.0.1", "5000");
        server.setBackend(Server.ServerBackend.REMOTE);

        GlobalEventBus.INSTANCE.get().register(new LoggerListener());

        Server.Message msg = server.sendCommand(Server.Command.LOGIN, "move");
        server.sendCommand(Server.Command.LOGIN, "test");

        System.out.println(msg);
        System.out.println(server);

        Window.start();
    }
}