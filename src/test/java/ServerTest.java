import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.server.Server;
import org.toop.server.backend.local.Local;
import org.toop.server.backend.remote.Remote;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    private Server server;

    @BeforeEach
    public void setUp() {
        server = new Server(Server.ServerBackend.LOCAL, "127.0.0.1", "8080");
    }

    @Test
    public void testConstructorSetsValues() {
        assertEquals("127.0.0.1", server.getIp());
        assertEquals("8080", server.getPort());
    }

    @Test
    public void testSetIpUpdatesValue() {
        server.setIp("192.168.1.1");
        assertEquals("192.168.1.1", server.getIp());
    }

    @Test
    public void testSetPortUpdatesValue() {
        server.setPort("9090");
        assertEquals("9090", server.getPort());
    }

    @Test
    public void testSetLocalBackend() {
        assertEquals(new Local(), server.getBackend());
    }

    @Test
    public void testSetRemoteBackend() {
        server.setBackend(Server.ServerBackend.REMOTE);
        assertEquals(new Remote(), server.getBackend());
    }

    @Test
    public void testNotNullAfterConstruction() {
        assertNotNull(server);
    }

}