package Server.Chatie.Class;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private final String senderID;
    private final String message;
    private final Date date;

    public Message(String senderID, String message) {
        this.senderID = senderID;
        this.message = message;
        date = new Date();
    }

    public String getSenderID() {
        return senderID;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }
}
