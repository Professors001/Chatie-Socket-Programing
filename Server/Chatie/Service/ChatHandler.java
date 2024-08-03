package Server.Chatie.Service;

import Server.Chatie.Class.Message;

import java.util.ArrayList;

public class ChatHandler {
    public static void printChat(ArrayList<Message> messages) {
        for (Message message : messages) {
            String displayname = UserManager.getUser(message.getSenderID()).getDisplayName();
            System.out.println(displayname + " | " + message.getDate());
            System.out.println("     " + message.getMessage() + "\n");
        }
    }
}
