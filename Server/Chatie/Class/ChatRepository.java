package Server.Chatie.Class;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChatRepository {
    private Map<String, Chat> chatMap;

    public ChatRepository() {
        chatMap = new HashMap<>();
    }

    public void addChat(Chat chat) {
        chatMap.put(chat.getRoomID(), chat);
    }

    public Chat getChat(String chatID) {
        return chatMap.get(chatID);
    }

    public boolean removeChat(String chatID) {
        return chatMap.remove(chatID) != null;
    }

    public Collection<Chat> getAllChat() {
        return chatMap.values();
    }
}
