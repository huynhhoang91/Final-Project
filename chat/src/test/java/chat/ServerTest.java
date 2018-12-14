package chat;

import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServerTest {

    private static final Map<String, Connection> testUsers = Server.getUsers();

    @Before
    public void putUsers() {
        testUsers.put("main room", new ConnectionTest());
    }

    @After
    public void clearUsers() {
        testUsers.clear();
    }

    @Test
    public void sendMessageToMainRoomTest() {
        String text = "test message to the main room";

        Server.sendBroadcastMessage(new Message(
                MessageType.TEXT,
                text,
                "TestUser"
        ));

        for (Map.Entry<String, Connection> pair : testUsers.entrySet()) {
            ConnectionTest c = (ConnectionTest) pair.getValue();
            assertTrue(pair.getKey() + " do not receive the " + text, c.isReceived);
        }
    }

    private static class ConnectionTest extends Connection {
        private boolean isReceived = false;

        @Override
        public void send(Message m) {
            isReceived = true;
        }
    }
}