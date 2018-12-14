package chat.client;

import chat.Connection;
import chat.ConsoleHelper;
import chat.Message;
import chat.MessageType;

import java.io.IOException;
import java.net.Socket;

/**
 * The client, at the beginning of its work, should: request from
 user address and port of the server, connect to the specified address, receive a request
 name from the server, ask the user name, send the user name to the server,
 wait for the server to accept the name. The client can then exchange text
 messages with the server. Messages will be exchanged in two parallel
 the running streams. One will be engaged in reading from the console and sending
 read to the server, and the second stream will receive data from the server and output them to
 console.
 *
 */
public class Client {
    /**
     * TCP connection that will be used to send and receive request from the server
     */
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {

        Client client = new Client();
        client.run();
    }

    /**
     * It needs to create the helper thread
     SocketThread, wait until he establishes a connection to the server, and then
     in a loop, read messages from the console and send them to the server. The exit condition
     from the loop will disable the client or enter the user command 'exit'.
     */
    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Error");
            return;
        }

        if (clientConnected) {
            ConsoleHelper.writeMessage("The connection is established. To exit, type 'exit'.");

            while (clientConnected) {
                String message;
                if (!(message = ConsoleHelper.readString()).equals("exit")) {
                    if (shouldSentTextFromConsole()) {
                        sendTextMessage(message);
                    }
                } else {
                    return;
                }
            }
        }
        else {
            ConsoleHelper.writeMessage("An error occurred while the client was running.");
        }
    }


    protected String getServerAddress() {

        ConsoleHelper.writeMessage("Enter the server address: ");
        return ConsoleHelper.readString();
    }


    protected int getServerPort() {

        ConsoleHelper.writeMessage("Enter the server port: ");
        return ConsoleHelper.readInt();
    }


    protected String getUserName() {

        ConsoleHelper.writeMessage("Enter the user name: ");
        return ConsoleHelper.readString();
    }


    protected boolean shouldSentTextFromConsole() {

        return true;
    }

    public ClientModel getModel() {
        return null;
    }

    protected SocketThread getSocketThread() {

        return new SocketThread();
    }


    /**
     * Send text to the server
     *
     * @param text the body of the message
     */
    protected void sendTextMessage(String text) {

        try {
            connection.send(new Message(MessageType.TEXT, text));

        } catch (IOException e) {
            ConsoleHelper.writeMessage("Send failed.");
            clientConnected = false;
        }
    }


    public class SocketThread extends Thread {

        public void run() {

            try {
                Socket socket = new Socket(getServerAddress(), getServerPort());
                Client.this.connection = new Connection(socket);


                clientHandshake();
                clientMainLoop();


            } catch (IOException e) {
                notifyConnectionStatusChanged(false);
            } catch (ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }

        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {

            while (true) {
                Message message = connection.receive();

                switch (message.getType()) {
                    case TEXT:
                        processIncomingMessage(message.getData());
                        break;
                    case USER_ADDED:
                        informAboutAddingNewUser(message.getData());
                        break;
                    case USER_REMOVED:
                        informAboutDeletingNewUser(message.getData());
                        break;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {

            while (true) {
                Message message = connection.receive();

                switch (message.getType()) {
                    case NAME_REQUEST: {
                        String userName = getUserName();
                        connection.send(new Message(MessageType.USER_NAME, userName));
                        break;
                    }
                    case NAME_ACCEPTED: {
                        notifyConnectionStatusChanged(true);
                        return;
                    }

                    default: {
                        throw new IOException("Unexpected MessageType");
                    }
                }
            }
        }


        /**
         * display the message text in the console
         *
         * @param message
         */
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }


        /**
         * to output in the console information that a member named userName has joined the chat
         *
         * @param userName the name of the user has joined to the chat
         */
        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("member  " + userName + " joined the chat");
        }


        /**
         * to output in the console information that a member named userName has left the chat
         *
         * @param userName the name of the user has left to the chat
         */
        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("member " + userName + " left the chat");
        }

        /**
         * Notify (awaken pending) the main thread of the Client class and change the value
         * of the clientConnected value
         *
         * @param clientConnected
         */
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }
    }
}
