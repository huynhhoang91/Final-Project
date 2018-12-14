package chat;


import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class HandlerTest {
    private static final String testUserName = "testUserName";
    @Test
    public void handshakeTest() {
        Server.Handler handler = new Server.Handler(new Socket());
        ConnectionTest con = new ConnectionTest();

        try {
            handler.serverHandshake(con);
        } catch (IOException e) {
            fail("serverHandshake have a IOException");
        } catch (ClassNotFoundException e) {
            fail("serverHandshake have a ClassNotFoundException");
        }

        Map<String, Connection> testUsers = Server.getUsers();

        assertTrue("User is not found", testUsers.containsKey(testUserName));
        assertTrue("Connection is not found", testUsers.containsValue(con));

        assertTrue("The first message sent is not 'NAME_REQUEST'",
                con.sendMessages.get(0).equals(MessageType.NAME_REQUEST));
    }
    private static class ConnectionTest extends Connection {
        private final List<MessageType> sendMessages = new ArrayList<>();

        @Override
        public Message receive() throws IOException, ClassNotFoundException {
            return new Message(MessageType.USER_NAME, testUserName);
        }

        @Override
        public void send(Message m) throws IOException {
            MessageType type = m.getType();
            sendMessages.add(type);
        }
    }
}
