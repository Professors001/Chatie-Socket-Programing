package Server.Chatie.Class;

import java.util.ArrayList;

public class Chat {
    private String roomID;
    private ArrayList<Message> messages;

    public Chat(String roomID) {
        this.roomID = roomID;
        messages = new ArrayList<Message>();
    }

    public String getRoomID() {
        return roomID;
    }

    public boolean addMessage(Message message) {
        return messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
