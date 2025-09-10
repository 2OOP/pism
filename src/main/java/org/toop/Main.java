package org.toop;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.Server;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(Server.ServerBackend.LOCAL, "127.0.0.1", "5000");
        server.setBackend(Server.ServerBackend.REMOTE);

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnCommand.class, e -> {
            if (e.command() == Server.Command.LOGIN) {
                System.out.println("LOGIN command -> " + String.join(" ", e.args()));
            }
            else if (e.command() == Server.Command.HELP) {
                System.out.println("HELP command -> " + String.join(" ", e.args()));
            }
        });

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnChangingServerBackend.class, e -> {
            System.out.println("Server backend has changed to -> " + e.backend());
        });

        Server.Message msg = server.sendCommand(Server.Command.LOGIN, "move");
        server.sendCommand(Server.Command.HELP, "test", "test2");

        server.setBackend(Server.ServerBackend.REMOTE);

        System.out.println(msg);
        System.out.println(server);

        Window.start();
    }
}