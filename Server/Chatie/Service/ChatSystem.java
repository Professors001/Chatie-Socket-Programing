package Server.Chatie.Service;

import Server.Chatie.Class.Chat;
import Server.Chatie.Class.ChatRepository;

public class ChatSystem {
    private static ChatRepository chatRepository = new ChatRepository();

    public static Chat getChat(String chatID) {
        return chatRepository.getChat(chatID);
    }

    public static boolean newChat(String chatID) {
        if (chatRepository.getChat(chatID) == null) {
            Chat newChat = new Chat(chatID);
            chatRepository.addChat(newChat);
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteChat(String chatID) {
        if (chatRepository.getChat(chatID) != null) {
            chatRepository.removeChat(chatID);
            return true;
        }
        return false;
    }

    public static ChatRepository getChatRepository() {
        return chatRepository;
    }
}