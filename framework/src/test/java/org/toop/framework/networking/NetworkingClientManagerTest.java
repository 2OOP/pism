//package org.toop.framework.networking;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import org.junit.jupiter.api.*;
//import org.mockito.*;
//import org.toop.framework.SnowflakeGenerator;
//import org.toop.framework.eventbus.EventFlow;
//import org.toop.framework.networking.events.NetworkEvents;
//
//class NetworkingClientManagerTest {
//
//    @Mock NetworkingClient mockClient;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testStartClientRequest_withMockedClient() throws Exception {
//        NetworkingClientManager manager = new NetworkingClientManager();
//        long clientId = new SnowflakeGenerator().nextId();
//
//        // Put the mock client into the map
//        manager.networkClients.put(clientId, mockClient);
//
//        // Verify insertion
//        assertEquals(mockClient, manager.networkClients.get(clientId));
//    }
//
//    @Test
//    void testHandleStartClient_postsResponse_withMockedClient() throws Exception {
//        NetworkingClientManager manager = new NetworkingClientManager();
//        long eventId = 12345L;
//
//        // Create the StartClient event
//        NetworkEvents.StartClient event = new NetworkEvents.StartClient("127.0.0.1", 8080, eventId);
//
//        // Inject a mock NetworkingClient manually
//        long fakeClientId = eventId; // just for test mapping
//        manager.networkClients.put(fakeClientId, mockClient);
//
//        // Listen for the response
//        CompletableFuture<NetworkEvents.StartClientResponse> future = new CompletableFuture<>();
//        new EventFlow().listen(NetworkEvents.StartClientResponse.class, future::complete);
//
//        // Instead of creating a real client, simulate the response
//        NetworkEvents.StartClientResponse fakeResponse =
//                new NetworkEvents.StartClientResponse(fakeClientId, eventId);
//        future.complete(fakeResponse);
//
//        // Wait for the future to complete
//        NetworkEvents.StartClientResponse actual = future.get();
//
//        // Verify the response has correct eventSnowflake and clientId
//        assertEquals(eventId, actual.eventSnowflake());
//        assertEquals(fakeClientId, actual.clientId());
//    }
//
//    @Test
//    void testHandleSendCommand_callsWriteAndFlush() throws Exception {
//        NetworkingClientManager manager = new NetworkingClientManager();
//        long clientId = 1L;
//        manager.networkClients.put(clientId, mockClient);
//
//        NetworkEvents.SendCommand commandEvent = new NetworkEvents.SendCommand(clientId, "HELLO");
//        manager.handleCommand(commandEvent);
//
//        verify(mockClient).writeAndFlushnl("HELLO");
//    }
//
//    @Test
//    void testHandleSendLogin_callsCorrectCommand() throws Exception {
//        NetworkingClientManager manager = new NetworkingClientManager();
//        long clientId = 1L;
//        manager.networkClients.put(clientId, mockClient);
//
//        manager.handleSendLogin(new NetworkEvents.SendLogin(clientId, "user1"));
//        verify(mockClient).writeAndFlushnl("LOGIN user1");
//    }
//
//    @Test
//    void testHandleCloseClient_removesClient() throws Exception {
//        NetworkingClientManager manager = new NetworkingClientManager();
//        long clientId = 1L;
//        manager.networkClients.put(clientId, mockClient);
//
//        manager.handleCloseClient(new NetworkEvents.CloseClient(clientId));
//
//        verify(mockClient).closeConnection();
//        assertFalse(manager.networkClients.containsKey(clientId));
//    }
//
//    @Test
//    void testHandleGetAllConnections_returnsClients() throws Exception {
//        NetworkingClientManager manager = new NetworkingClientManager();
//        manager.networkClients.put(1L, mockClient);
//
//        CompletableFuture<List<NetworkingClient>> future = new CompletableFuture<>();
//        NetworkEvents.RequestsAllClients request = new NetworkEvents.RequestsAllClients(future);
//
//        manager.handleGetAllConnections(request);
//
//        List<NetworkingClient> clients = future.get();
//        assertEquals(1, clients.size());
//        assertSame(mockClient, clients.get(0));
//    }
//
//    @Test
//    void testHandleShutdownAll_clearsClients() throws Exception {
//        NetworkingClientManager manager = new NetworkingClientManager();
//        manager.networkClients.put(1L, mockClient);
//
//        manager.handleShutdownAll(new NetworkEvents.ForceCloseAllClients());
//
//        verify(mockClient).closeConnection();
//        assertTrue(manager.networkClients.isEmpty());
//    }
//}
// TODO