package org.toop.framework.networking;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.toop.framework.SnowflakeGenerator;
import org.toop.framework.eventbus.EventFlow;
import org.toop.framework.networking.events.NetworkEvents;

class NetworkingClientManagerTest {

    @Mock NetworkingClient mockClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartClientRequest_withMockedClient() throws Exception {
        NetworkingClientManager manager = new NetworkingClientManager();

        NetworkingClient mockClient = mock(NetworkingClient.class);

        long clientId = new SnowflakeGenerator().nextId();

        // Directly put mock into the map
        manager.networkClients.put(clientId, mockClient);

        // Verify it exists
        assertEquals(mockClient, manager.networkClients.get(clientId));
    }

    @Test
    void testHandleStartClient_postsResponse() throws Exception {
        NetworkingClientManager manager = new NetworkingClientManager();
        long eventId = 12345L;

        NetworkEvents.StartClient event = new NetworkEvents.StartClient("127.0.0.1", 8080, eventId);

        CompletableFuture<NetworkEvents.StartClientResponse> future = new CompletableFuture<>();

        // Listen for response
        new EventFlow().listen(NetworkEvents.StartClientResponse.class, future::complete);

        manager.handleStartClient(event);

        NetworkEvents.StartClientResponse response = future.get(); // blocks until completed
        assertEquals(eventId, response.eventSnowflake());
    }

    @Test
    void testHandleSendCommand_callsWriteAndFlush() throws Exception {
        NetworkingClientManager manager = spy(new NetworkingClientManager());
        long clientId = 1L;

        manager.networkClients.put(clientId, mockClient);

        NetworkEvents.SendCommand commandEvent = new NetworkEvents.SendCommand(clientId, "HELLO");

        manager.handleCommand(commandEvent);

        verify(mockClient).writeAndFlushnl("HELLO");
    }

    @Test
    void testHandleSendLogin_callsCorrectCommand() throws Exception {
        NetworkingClientManager manager = spy(new NetworkingClientManager());
        long clientId = 1L;
        manager.networkClients.put(clientId, mockClient);

        manager.handleSendLogin(new NetworkEvents.SendLogin(clientId, "user1"));
        verify(mockClient).writeAndFlushnl("LOGIN user1");
    }

    @Test
    void testHandleCloseClient_removesClient() throws Exception {
        NetworkingClientManager manager = spy(new NetworkingClientManager());
        long clientId = 1L;
        manager.networkClients.put(clientId, mockClient);

        manager.handleCloseClient(new NetworkEvents.CloseClient(clientId));

        verify(mockClient).closeConnection();
        assertFalse(manager.networkClients.containsKey(clientId));
    }

    @Test
    void testHandleGetAllConnections_returnsClients() throws Exception {
        NetworkingClientManager manager = new NetworkingClientManager();
        manager.networkClients.put(1L, mockClient);

        CompletableFuture<List<NetworkingClient>> future = new CompletableFuture<>();
        NetworkEvents.RequestsAllClients request = new NetworkEvents.RequestsAllClients(future);

        manager.handleGetAllConnections(request);

        List<NetworkingClient> clients = future.get();
        assertEquals(1, clients.size());
        assertSame(mockClient, clients.getFirst());
    }

    @Test
    void testHandleShutdownAll_clearsClients() throws Exception {
        NetworkingClientManager manager = new NetworkingClientManager();
        manager.networkClients.put(1L, mockClient);

        manager.handleShutdownAll(new NetworkEvents.ForceCloseAllClients());

        verify(mockClient).closeConnection();
        assertTrue(manager.networkClients.isEmpty());
    }
}
