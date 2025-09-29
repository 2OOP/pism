package org.toop;

import java.util.Arrays;
import org.toop.app.gui.LocalServerSelector;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.NetworkingClientManager;
import org.toop.framework.networking.NetworkingInitializationException;
import org.toop.framework.networking.events.NetworkEvents;

public class Main {
    static void main(String[] args) {
        initSystems();

        EventFlow a =
                new EventFlow()
                        .addPostEvent(NetworkEvents.StartClient.class, "127.0.0.1", 7789)
                        .onResponse(Main::login)
                        //			.onResponse(Main::sendCommand)
                        //			.onResponse(Main::closeClient)
                        .asyncPostEvent();

        new Thread(
                        () -> {
                            while (a.getResult() == null) {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                }
                            }
                            long clid = (Long) a.getResult().get("clientId");
                            new EventFlow()
                                    .addPostEvent(
                                            new NetworkEvents.SendSubscribe(clid, "tic-tac-toe"))
                                    .listen(
                                            NetworkEvents.PlayerlistResponse.class,
                                            response -> {
                                                if (response.clientId() == clid)
                                                    System.out.println(
                                                            Arrays.toString(response.playerlist()));
                                            })
                                    .listen(
                                            NetworkEvents.ChallengeResponse.class,
                                            response -> {
                                                if (response.clientId() == clid)
                                                    System.out.println(response.challengeId());
                                            })
                                    .listen(
                                            NetworkEvents.ChallengeCancelledResponse.class,
                                            response -> {
                                                if (response.clientId() == clid)
                                                    System.out.println(response.challengeId());
                                            })
                                    .listen(
                                            NetworkEvents.GamelistResponse.class,
                                            response -> {
                                                if (response.clientId() == clid)
                                                    System.out.println(
                                                            Arrays.toString(response.gamelist()));
                                            })
                                    .asyncPostEvent();
                        })
                .start();

        new Thread(() -> javax.swing.SwingUtilities.invokeLater(LocalServerSelector::new)).start();
    }

    private static void login(NetworkEvents.StartClientResponse event) {
        new Thread(
                        () -> {
                            try {
                                Thread.sleep(1000);
                                new EventFlow()
                                        .addPostEvent(
                                                new NetworkEvents.SendCommand(
                                                        event.clientId(), "login bas"))
                                        .asyncPostEvent();
                            } catch (InterruptedException e) {
                            }
                        })
                .start();
    }

    private static void initSystems() throws NetworkingInitializationException {
        new NetworkingClientManager();
    }
}
