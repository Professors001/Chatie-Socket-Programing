package Server.Chatie.Class;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public class UserRepository {
    private Map<String, User> users;

    public UserRepository() {
        users = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUser(String userID) {
        return users.get(userID);
    }

    public boolean removeUser(String userID) {
        return users.remove(userID) != null;
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}