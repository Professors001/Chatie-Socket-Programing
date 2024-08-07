import Server.Chatie.Class.Message;
import Server.Chatie.Class.User;
import Server.Chatie.Service.ChatHandler;
import Server.Chatie.Service.ChatSystem;
import Server.Chatie.Service.UserManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream())) {

            while (true) {
                // Read the request from the client
                Response request = (Response) ois.readObject();
                System.out.println("Received request: " + request);
                Response response = new Response("404_UNKNOWN_REQUEST", null);

                // Process the request
                String statusCode = request.getStatusCode();
                if (statusCode.equals("REGISTER")) {
                    String[] tuple = (String[]) request.getData();
                    if (UserManager.registerUser(tuple[0], tuple[1])) {
                        System.out.println("User " + tuple[0] + " has been registered.");
                        response.setStatusCode("200_REGISTER_S");
                        response.setData(UserManager.getUser(tuple[0]));
                    } else {
                        System.out.println("User " + tuple[0] + " has not been registered.");
                        response.setStatusCode("300_REGISTER_US");
                        response.setData(null);
                    }
                } else if (statusCode.equals("LOGIN")) {
                    String[] tuple = (String[]) request.getData();
                    if (UserManager.loginUser(tuple[0], tuple[1])) {
                        System.out.println("User " + tuple[0] + " has been logged in.");
                        response.setStatusCode("200_LOGIN_S");
                        response.setData(UserManager.getUser(tuple[0]));
                    } else {
                        System.out.println("User " + tuple[0] + " has not been logged in.");
                        response.setStatusCode("300_LOGIN_US");
                        response.setData(null);
                    }
                } else if (statusCode.equals("JOINING")) {
                    String roomID = (String) request.getData();
                    if (ChatSystem.getChat(roomID) != null) {
                        System.out.println("Room " + roomID + " has already been joined.");
                        response.setStatusCode("200_JOIN_S");
                        response.setData(ChatSystem.getChat(roomID));
                    } else {
                        System.out.println("Room " + roomID + " has not been joined.");
                        response.setStatusCode("300_JOIN_US");
                        response.setData(null);
                    }
                } else if (statusCode.equals("CREATING")) {
                    String roomID = (String) request.getData();
                    if (ChatSystem.getChat(roomID) == null) {
                        ChatSystem.newChat(roomID);
                        System.out.println("Room " + roomID + " has been created.");
                        response.setStatusCode("200_CREATING_S");
                        response.setData(null);
                    } else {
                        System.out.println("Room " + roomID + " has not been created.");
                        response.setStatusCode("300_CREATING_US");
                        response.setData(null);
                    }
                } else if (statusCode.equals("REQUEST_CHAT")) {
                    String roomID = (String) request.getData();
                    if (ChatSystem.getChat(roomID) != null) {
                        response.setStatusCode("200_CHAT_S");
                        response.setData(ChatSystem.getChat(roomID));
                    } else {
                        response.setStatusCode("300_CHAT_US");
                        response.setData(null);
                    }
                }
                else if (statusCode.equals("SENDING")) {
                    String[] tuple = (String[]) request.getData();
                    if (ChatSystem.getChat(tuple[0]) != null) {
                        System.out.println("Room " + tuple[0] + " has already been update Message.");
                        Message newMessage = new Message(tuple[1], tuple[2]);
                        ChatSystem.getChat(tuple[0]).addMessage(newMessage);
                        response.setStatusCode("200_SENDING_S");
                        response.setData(null);
                    }
                }
                ops.writeObject(response);
                System.out.println("Sent response: " + response);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}