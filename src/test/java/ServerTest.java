import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.toop.server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    private Server server;

    @BeforeEach
    public void setUp() {
        server = new Server("127.0.0.1", "8080");
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
    public void testNotNullAfterConstruction() {
        assertNotNull(server);
    }

}