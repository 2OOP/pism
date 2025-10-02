package org.toop.framework.networking.events;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;

class NetworkEventsTest {

    @Test
    void testRequestsAllClients() {
        CompletableFuture<List<String>> future = new CompletableFuture<>();
        NetworkEvents.RequestsAllClients event =
                new NetworkEvents.RequestsAllClients((CompletableFuture) future);
        assertNotNull(event.future());
        assertEquals(future, event.future());
    }

    @Test
    void testForceCloseAllClients() {
        NetworkEvents.ForceCloseAllClients event = new NetworkEvents.ForceCloseAllClients();
        assertNotNull(event);
    }

    @Test
    void testChallengeCancelledResponse() {
        NetworkEvents.ChallengeCancelledResponse event =
                new NetworkEvents.ChallengeCancelledResponse(42L, "ch123");
        assertEquals(42L, event.clientId());
        assertEquals("ch123", event.challengeId());
    }

    @Test
    void testChallengeResponse() {
        NetworkEvents.ChallengeResponse event =
                new NetworkEvents.ChallengeResponse(1L, "Alice", "Chess", "ch001");
        assertEquals("Alice", event.challengerName());
        assertEquals("Chess", event.gameType());
        assertEquals("ch001", event.challengeId());
    }

    @Test
    void testPlayerlistResponse() {
        String[] players = {"p1", "p2"};
        NetworkEvents.PlayerlistResponse event = new NetworkEvents.PlayerlistResponse(5L, players);
        assertArrayEquals(players, event.playerlist());
    }

    @Test
    void testStartClientResultAndSnowflake() {
        NetworkEvents.StartClient event = new NetworkEvents.StartClient("127.0.0.1", 9000, 12345L);
        assertEquals("127.0.0.1", event.ip());
        assertEquals(9000, event.port());
        assertEquals(12345L, event.eventSnowflake());

        Map<String, Object> result = event.result();
        assertEquals("127.0.0.1", result.get("ip"));
        assertEquals(9000, result.get("port"));
        assertEquals(12345L, result.get("eventSnowflake"));
    }

    @Test
    void testStartClientResponseResultAndSnowflake() {
        NetworkEvents.StartClientResponse response =
                new NetworkEvents.StartClientResponse(99L, 54321L);
        assertEquals(99L, response.clientId());
        assertEquals(54321L, response.eventSnowflake());

        Map<String, Object> result = response.result();
        assertEquals(99L, result.get("clientId"));
        assertEquals(54321L, result.get("eventSnowflake"));
    }

    @Test
    void testSendCommandVarargs() {
        NetworkEvents.SendCommand event = new NetworkEvents.SendCommand(7L, "LOGIN", "Alice");
        assertEquals(7L, event.clientId());
        assertArrayEquals(new String[] {"LOGIN", "Alice"}, event.args());
    }

    @Test
    void testReceivedMessage() {
        NetworkEvents.ReceivedMessage msg = new NetworkEvents.ReceivedMessage(11L, "Hello");
        assertEquals(11L, msg.clientId());
        assertEquals("Hello", msg.message());
    }

    @Test
    void testClosedConnection() {
        NetworkEvents.ClosedConnection event = new NetworkEvents.ClosedConnection(22L);
        assertEquals(22L, event.clientId());
    }

    // Add more one-liners for the rest of the records to ensure constructor works
    @Test
    void testOtherRecords() {
        NetworkEvents.SendLogin login = new NetworkEvents.SendLogin(1L, "Bob");
        assertEquals(1L, login.clientId());
        assertEquals("Bob", login.username());

        NetworkEvents.SendLogout logout = new NetworkEvents.SendLogout(2L);
        assertEquals(2L, logout.clientId());

        NetworkEvents.SendGetPlayerlist getPlayerlist = new NetworkEvents.SendGetPlayerlist(3L);
        assertEquals(3L, getPlayerlist.clientId());

        NetworkEvents.SendGetGamelist getGamelist = new NetworkEvents.SendGetGamelist(4L);
        assertEquals(4L, getGamelist.clientId());

        NetworkEvents.SendSubscribe subscribe = new NetworkEvents.SendSubscribe(5L, "Chess");
        assertEquals(5L, subscribe.clientId());
        assertEquals("Chess", subscribe.gameType());

        NetworkEvents.SendMove move = new NetworkEvents.SendMove(6L, (short) 1);
        assertEquals(6L, move.clientId());
        assertEquals((short) 1, move.moveNumber());

        NetworkEvents.SendChallenge challenge = new NetworkEvents.SendChallenge(7L, "Eve", "Go");
        assertEquals(7L, challenge.clientId());
        assertEquals("Eve", challenge.usernameToChallenge());
        assertEquals("Go", challenge.gameType());

        NetworkEvents.SendAcceptChallenge accept = new NetworkEvents.SendAcceptChallenge(8L, 100);
        assertEquals(8L, accept.clientId());
        assertEquals(100, accept.challengeId());

        NetworkEvents.SendForfeit forfeit = new NetworkEvents.SendForfeit(9L);
        assertEquals(9L, forfeit.clientId());

        NetworkEvents.SendMessage message = new NetworkEvents.SendMessage(10L, "Hi!");
        assertEquals(10L, message.clientId());
        assertEquals("Hi!", message.message());

        NetworkEvents.SendHelp help = new NetworkEvents.SendHelp(11L);
        assertEquals(11L, help.clientId());

        NetworkEvents.SendHelpForCommand helpForCommand =
                new NetworkEvents.SendHelpForCommand(12L, "MOVE");
        assertEquals(12L, helpForCommand.clientId());
        assertEquals("MOVE", helpForCommand.command());

        NetworkEvents.CloseClient close = new NetworkEvents.CloseClient(13L);
        assertEquals(13L, close.clientId());

        NetworkEvents.ServerResponse serverResponse = new NetworkEvents.ServerResponse(14L);
        assertEquals(14L, serverResponse.clientId());

        NetworkEvents.Reconnect reconnect = new NetworkEvents.Reconnect(15L);
        assertEquals(15L, reconnect.clientId());

        NetworkEvents.ChangeClientHost change =
                new NetworkEvents.ChangeClientHost(16L, "localhost", 1234);
        assertEquals(16L, change.clientId());
        assertEquals("localhost", change.ip());
        assertEquals(1234, change.port());

        NetworkEvents.CouldNotConnect couldNotConnect = new NetworkEvents.CouldNotConnect(17L);
        assertEquals(17L, couldNotConnect.clientId());
    }
}
