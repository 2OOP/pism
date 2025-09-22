package org.toop;

import org.toop.eventbus.EventFlow;
import org.toop.eventbus.GlobalEventBus;
import org.toop.eventbus.events.NetworkEvents;
import org.toop.frontend.networking.NetworkingGameClientHandler;

import java.util.function.Supplier;

public class MainTest {
    MainTest() {
        var a = new EventFlow()
                .addPostEvent(NetworkEvents.StartClient.class,
                        (Supplier<NetworkingGameClientHandler>) NetworkingGameClientHandler::new,
                        "127.0.0.1",
                        5001)
                .onResponse(NetworkEvents.StartClientSuccess.class)
                .perform(this::handleStartClientSuccess)
                .unsubscribeAfterSuccess().asyncPostEvent();
    }

    private void handleStartClientSuccess(NetworkEvents.StartClientSuccess event) {
        GlobalEventBus.post(new NetworkEvents.CloseClient(event.clientId()));
    }
}
