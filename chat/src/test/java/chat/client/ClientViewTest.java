package chat.client;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientViewTest {
    private static final String ownName = "ownName";

    private static ClientView client;

    @BeforeClass
    public static void createClient() {
        client = new ClientView("Test");
        client.setClientName(ownName);
    }

    @Test
    public void changeConnectionStatusTrue() {
        client.notifyConnectionStatusChanged(true);

        assertTrue("Client connected is not true", client.isConnected());
    }

    @Test
    public void changeConnectionStatusFalse() {
        client.notifyConnectionStatusChanged(false);

        assertFalse("Client connected is not false", client.isConnected());
    }
}