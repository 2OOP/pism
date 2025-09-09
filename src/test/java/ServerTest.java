import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.toop.Server;

public class ServerTest {

    private Server server;

    @Before
    public void setUp() {
        server = new Server("127.0.0.1", "8080");
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
        Assert.assertEquals("909", server.getPort());
    }

    @Test
    public void testNotNullAfterConstruction() {
        Assert.assertNotNull(server);
    }
}