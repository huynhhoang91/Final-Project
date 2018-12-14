package chat.client;


import chat.Message;
import chat.MessageType;

import java.io.IOException;

/**
 * The controller of the MVC pattern. Optionally updates the model value and access the view to update it.
 * This class receives requests from view to send messages and sends them to the server part.
 */
public class ClientController extends Client {
    private ClientModel model = new ClientModel();
    private ClientView view = new ClientView(this);

    /**
     * Start the controller, that create view and display it to the user
     */
    public static void main(String[] args) {
        ClientController controller = new ClientController();
        controller.run();
    }


    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }

    @Override
    public void run() {
        getSocketThread().run();
    }

    @Override
    public ClientModel getModel() {
        return model;
    }

    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }

    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }

    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    /**
     * A thread that handles all requests coming from the server on the client side
     * and is responsible for the first acquaintance of the client and the server
     */
    public class GuiSocketThread extends SocketThread {
        /**
         * Handshake between the server and client. Client send the name of the user back to the server and wait
         * until server will accept or decline the client.
         */
        @Override
        protected void clientHandshake() throws IOException, ClassNotFoundException {
            String usrName = null;
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    usrName = getUserName();
                    connection.send(new Message(MessageType.USER_NAME, usrName));
                    continue;
                }
                if (message.getType() == MessageType.NAME_ACCEPTED) {
                    view.setClientName(usrName);
                    notifyConnectionStatusChanged(true);
                    break;
                } else
                    throw new IOException("Unexpected MessageType");
            }
        }

        /**
         * Process the incoming message and output it using GUI
         *
         * @param message the message need to output
         */
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        /**
         * Inform other online users that new member has joined the chat
         *
         * @param userName the name of the user has joined to the chat
         */
        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        /**
         * Inform other online users that the member with name userName has left the chat
         *
         * @param userName the name of the user has left to the chat
         */
        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        /**
         * If the server accept the client. GUI will inform the user about it
         *
         * @param clientConnected
         */
        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }

}
