package Server.Chatie.Service;

import Server.Chatie.Class.User;
import Server.Chatie.Class.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserManager {
    private static UserRepository userRepository = new UserRepository();
    private static final String SALT = "a1b2c3d4";

    public static boolean registerUser(String username, String password, String displayName) {
        if (userRepository.getUser(username) != null) {
            return false;
        }

        String encryptedPassword = encryptPassword(password);
        User newUser = new User(username, encryptedPassword, displayName);
        userRepository.addUser(newUser);

        return true;
    }

    public static boolean loginUser(String username, String password) {
        User user = userRepository.getUser(username);
        if (user != null) {
            return user.getPassword().equals(encryptPassword(password));
        }
        return false;
    }

    public static User getUser(String username) {
        return userRepository.getUser(username);
    }

    public static boolean removeUser(String userID) {
        return userRepository.removeUser(userID);
    }

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    private static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((password + SALT).getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}