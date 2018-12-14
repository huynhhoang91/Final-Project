package chat;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceManagerTest {

    @Test
    public void getPortTest() {
        ResourceManager resourceManager = ResourceManager.getInstance();
        int port = resourceManager.getPort();
        assertEquals(port, 8080);
    }
}