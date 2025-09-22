package org.toop;

import com.google.common.base.Supplier;
import org.toop.eventbus.EventPublisher;
import org.toop.eventbus.GlobalEventBus;
import org.toop.eventbus.events.Events;
import org.toop.eventbus.events.NetworkEvents;
import org.toop.frontend.networking.NetworkingGameClientHandler;

public class MainTest {

    MainTest() {

        var ep = new EventPublisher<>(
                Events.ServerEvents.StartServer.class,
                5001,
                "tictactoe"
        ).onEvent(
                this::handleServerStarted
        ).unregisterAfterSuccess().postEvent();

//        var ep = new EventPublisher<>(
//                NetworkEvents.SendCommand.class,
//                (Supplier<NetworkingGameClientHandler>) NetworkingGameClientHandler::new,
//                "127.0.0.1",
//                5001
//        ).onEventById(this::handleStartClientRequest).unregisterAfterSuccess().postEvent();
    }

    private void handleStartClientRequest(NetworkEvents.StartClientSuccess event) {
        GlobalEventBus.post(new NetworkEvents.CloseClient((String) event.connectionId()));
    }

    private void handleServerStarted(Events.ServerEvents.ServerStarted event) {
        System.out.println("Server started");
    }


}
