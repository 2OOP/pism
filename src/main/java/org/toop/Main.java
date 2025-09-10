package org.toop;

import org.toop.eventbus.EventRegistry;
import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;
import org.toop.server.Server;

public class Main {
    public static void main(String[] args) {

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnChangingServerBackend.class, e -> {
            System.out.println("Server backend has changed to -> " + e.backend());
        });

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnChangingServerIp.class, e -> {
            System.out.println("Server ip has changed to -> " + e.ip());
        });

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnChangingServerPort.class, e -> {
            System.out.println("Server port has changed to -> " + e.port());
        });

        Server server = new Server(Server.ServerBackend.LOCAL, "127.0.0.1", "5000");
        server.setBackend(Server.ServerBackend.REMOTE);

        GlobalEventBus.subscribeAndRegister(Events.ServerEvents.OnCommand.class, e -> {
            if (e.command() == Server.Command.LOGIN) {
                System.out.println("LOGIN command -> " + String.join(" ", e.args()));
            }
            else if (e.command() == Server.Command.HELP) {
                System.out.println("HELP command -> " + String.join(" ", e.args()));
            }
            else {
                System.out.println(e.command().toString());
            }
        });

        Server.Message msg = server.sendCommand(Server.Command.LOGIN, "move");
        server.sendCommand(Server.Command.HELP, "test", "test2");
        server.sendCommand(Server.Command.BYE);

        GlobalEventBus.post(new Events.ServerEvents.changeServerIp("127.1.1.1"));
        GlobalEventBus.post(new Events.ServerEvents.changeServerPort("5003"));
        server.setBackend(Server.ServerBackend.REMOTE);

        System.out.println(msg);
        System.out.println(server);

        Window.start();
    }
}