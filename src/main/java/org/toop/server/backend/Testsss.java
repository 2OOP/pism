package org.toop.server.backend;

import org.toop.eventbus.Events;
import org.toop.eventbus.GlobalEventBus;

public class Testsss extends Thread {

    public Testsss() {}

    public void run() {
        while (true) {
            try {
                sleep(100);
                GlobalEventBus.post(new Events.ServerEvents.command("HELP", "TEST"));
                sleep(1000);
                GlobalEventBus.post(new Events.ServerEvents.ChangeConnection("127.0.0.1", "5001"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void start(String keepEmpty) {
        new Testsss().start();
    }

}
