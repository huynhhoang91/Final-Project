package chat;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {
    private final MessageType type;
    private final String data;
    private final String sender;

    public Message(MessageType type){
        this.type = type;
        data     = null;
        sender = null;
    }
    public Message(MessageType type, String data){
        this.type = type;
        this.data = data;
        sender = null;
    }

    /**
     * Constructor for testing
     */
    public Message(MessageType type, String data, String sender) {
        this.type = type;
        this.data = data;
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }
    public String getData() {
        return data;
    }
    public String getSender() {
        return sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return type == message.type &&
                Objects.equals(data, message.data) &&
                Objects.equals(sender, message.sender);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, data, sender);
    }
}
