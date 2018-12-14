package chat;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main class of this application. Handles all customer requests.
 * Provides multithreading because it allocates a separate thread for each client.
 * In addition, the server notifies all users of new events
 *
 * version 1.0
 */
public class Server {
    private static final Logger log = Logger.getLogger(Server.class);
    /**
     * Stores the names of all users who are currently connected to the server,
     * as well as the TCP connection to send them messages
     */
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    /**
     * Implements the Protocol of communication with the client
     */
    public static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * The process of meeting the client with the server. The server asks the client for its name.
         * Checks if this name can be used for the connection.
         * If successful, the connection between the client and the server will be established.
         * The method takes connection as a parameter, and returns the name of the new client.
         *
         * @param connection TCP connection
         * @return the name of the new client
         */
        public String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            boolean flag = true;
            Message request = null;
            while (flag) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                ConsoleHelper.writeMessage(String.format("NAME_REQUEST IS SENDED TO CONNECTION [%s]", connection.getRemoteSocketAddress()));
                request = connection.receive();
                if (request.getType() != MessageType.USER_NAME)
                    continue;
                else if (!request.getData().equals("") && request.getData() != null && request.getType() == MessageType.USER_NAME) {
                        if (!connectionMap.containsKey(request.getData())) {
                            connectionMap.put(request.getData(), connection);
                            ConsoleHelper.writeMessage("USER_NAME RECEIVED. NAME IS ACCEPTED");
                            ConsoleHelper.writeMessage("USER_ACCEPTED IS SENDED TO THE " + request.getData());
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            ConsoleHelper.writeMessage("MEETING WITH " + request.getData() + " IS SUCCESFULL.");
                            flag = false;
                        }
                    }
                }
            return request.getData();
        }

        /**
         * Notifies users that a new member has joined the chat
         *
         * @param connection TCP connection
         * @param userName the name of the user joined the chat
         */
        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (String key : connectionMap.keySet()) {
                if (!key.equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, key));
                }
            }
        }

        /**
         * A method that processes client requests in an infinite loop
         * and notifies other users that a new message has arrived.
         *
         * @param connection TCP connection
         * @param userName the name of the user has sent the message
         */
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                ConsoleHelper.writeMessage("THE MESSAGE IS RECEIVED FROM " + userName);
                if (message.getType() == MessageType.TEXT) {
                    ConsoleHelper.writeMessage("THE MESSAGE WITH MESSAGE.TYPE [TEXT] IS SENDED FROM " + userName + " TO ALL.");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy hh:mm");
                    Date date = new Date();
                    String formattedDate = "<" + simpleDateFormat.format(date) + ">";
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + formattedDate + " : " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Error");
                }
            }
        }

        /**
         * Encapsulates the entire process of the client's acquaintance with the server after the connection is established.
         * Additionally, runs a method that notifies other users that a new member has joined
         */
        public void run(){
            Connection connection = null;
            String clientName = null;
            try {
                ConsoleHelper.writeMessage(String.format("THE CONNECTION WITH REMOTELY ADDRESS [%s] IS COMPLETE!", socket.getRemoteSocketAddress()));
                connection = new Connection(socket);
                clientName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, clientName));
                sendListOfUsers(connection, clientName);
                serverMainLoop(connection, clientName);
            } catch (IOException e) {
                ConsoleHelper.writeMessage(e.getMessage());
            } catch (ClassNotFoundException e) {
                ConsoleHelper.writeMessage(e.getMessage());
            }
            if (clientName != null) {
                connectionMap.remove(clientName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, clientName));
            }
            ConsoleHelper.writeMessage(String.format("THE CONNECTION WITH REMOTELY ADDRESS [%s] IS CLOSED.", socket.getRemoteSocketAddress()));
        }
    }

    /**
     * A helper method that encapsulates the process of sending a message to all online users except the sender
     *
     * @param message the message need to send, contains all the information about the message/
     */
    public static void sendBroadcastMessage(Message message){
        for (Connection map:
             connectionMap.values()) {
            try {
                map.send(message);
                System.out.println(String.format("%s sent message '%s' to the main room.",
                        message.getSender(), message.getType()));
            } catch (IOException e) {
                System.out.println("Error");
            }
        }
    }

    /**
     * main method. Start the server and wait until some client will connected.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        ResourceManager resourceManager = ResourceManager.getInstance();
        int port = resourceManager.getPort();
        try {
            serverSocket = new ServerSocket(port);
            //System.out.println("SERVER IS STARTED!");
            Server server = new Server();
            Method writeMessage = ConsoleHelper.class.getDeclaredMethod("writeMessage", String.class);
            writeMessage.setAccessible(true);
            writeMessage.invoke(server, new String("Server is started"));
            while (true) {
                new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            serverSocket.close();
        }
    }

    protected static Map<String, Connection> getUsers() {
        return connectionMap;
    }
}
