import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.toop.server.Server;
import org.toop.server.backend.*;

public class TestServerTest {

    private Server server;

    @Before
    public void setUp() {
        server = new Server(Server.ServerBackend.LOCAL, "127.0.0.1", "8080");
    }

    @Test
    public void testConstructorSetsValues() {
        Assert.assertEquals("127.0.0.1", server.getIp());
        Assert.assertEquals("8080", server.getPort());
    }

    @Test
    public void testSetIpUpdatesValue() {
        server.setIp("192.168.1.1");
        Assert.assertEquals("192.168.1.1", server.getIp());
    }

    @Test
    public void testSetPortUpdatesValue() {
        server.setPort("9090");
        Assert.assertEquals("9090", server.getPort());
    }

    @Test
    public void testSetLocalBackend() {
        Assert.assertEquals(new Local(), server.getBackend());
    }

    @Test
    public void testSetRemoteBackend() {
        server.setBackend(Server.ServerBackend.REMOTE);
        Assert.assertEquals(new Remote(), server.getBackend());
    }

    @Test
    public void testNotNullAfterConstruction() {
        Assert.assertNotNull(server);
    }

}