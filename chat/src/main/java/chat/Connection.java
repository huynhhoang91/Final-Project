package chat;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    /**
     * Constructor for testing
     */
    public Connection() {
        socket = null;
        out = null;
        in = null;
    }

    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());
    }
    public void send(Message message) throws IOException{
        synchronized (out) {
            out.writeObject(message);
        }
    }
    public Message receive() throws IOException, ClassNotFoundException{
        Message message;
        synchronized (in){
            message = (Message)in.readObject();
        }
        return message;
    }
    public void close() throws IOException{
        socket.close();
        in.close();
        out.close();
    }
    public SocketAddress getRemoteSocketAddress(){
        if (socket != null)
            return socket.getRemoteSocketAddress();
        return null;
    }
}
