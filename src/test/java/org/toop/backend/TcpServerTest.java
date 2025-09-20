package org.toop.backend;

import org.junit.jupiter.api.*;
import org.toop.backend.tictactoe.ParsedCommand;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TcpServerTest {

    static class TestTcpServer extends TcpServer {
        public TestTcpServer(int port) throws IOException {
            super(port);
        }

        @Override
        public void run() {
            // Call super.run() in a separate thread if needed
            super.run();
        }
    }

    private TestTcpServer server;
    private Thread serverThread;
    private int port = 12345;

    @BeforeEach
    void setup() throws IOException {
        server = new TestTcpServer(port);
        serverThread = new Thread(server::run);
        serverThread.start();
    }

    @AfterEach
    void teardown() {
        server.stop();
        try {
            serverThread.join(1000);
        } catch (InterruptedException ignored) {}
    }

    @Test
    void testServerStartsAndStops() {
        assertTrue(server.isRunning());
        server.stop();
        assertFalse(server.isRunning());
    }

    @Test
    void testClientMessageEnqueued() throws IOException, InterruptedException {
        Socket client = new Socket("localhost", port);
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);

        String message = "hello server";
        out.println(message);

        String received = server.receivedQueue.poll(1, TimeUnit.SECONDS);
        assertEquals(message, received);

        client.close();
    }

    @Test
    void testSendQueueSendsToClient() throws IOException, InterruptedException {
        Socket client = new Socket("localhost", port);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String msg = "test message";
        server.sendQueue.put(msg);

        // The server may need some time to deliver
        String received = in.readLine();
        assertEquals(msg, received);

        client.close();
    }

//    @Test
//    void testGetNewestCommand() throws InterruptedException {
//        String command = "move 1 2";
//        server.receivedQueue.put(command);
//
//        ParsedCommand parsed = server.getNewestCommand();
//        System.out.println(parsed);
//        assertNotNull(parsed);
//        assertEquals(command, parsed.returnMessage); TODO: Test later
//    }

    @Test
    void testMultipleClients() throws IOException, InterruptedException {
        Socket client1 = new Socket("localhost", port);
        Socket client2 = new Socket("localhost", port);

        PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);

        out1.println("msg1");
        out2.println("msg2");

        assertEquals("msg1", server.receivedQueue.poll(1, TimeUnit.SECONDS));
        assertEquals("msg2", server.receivedQueue.poll(1, TimeUnit.SECONDS));

        client1.close();
        client2.close();
    }
}