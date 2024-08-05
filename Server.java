import Server.Chatie.Class.User;
import Server.Chatie.Service.ChatSystem;
import Server.Chatie.Service.UserManager;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 65432;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening for connections...");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connection from " + clientSocket.getInetAddress());
                    new ClientHandler(clientSocket).start();
                } catch (IOException e) {
                    System.out.println("Error accepting connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                 ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {

                Response requestObject;
                Response responseObject = new Response("404_ERROR", null);

                while (true) {
                    try {
                        requestObject = (Response) inputStream.readObject();

                        if (requestObject.getStatusCode().equals("REGISTER")) {
                            User user = (User) requestObject.getData();
                            if (UserManager.registerUser(user.getUsername(), user.getPassword(), user.getDisplayName())) {
                                System.out.println("User " + user.getUsername() + " has been registered.");
                                responseObject.setStatusCode("200_REGISTER_S");
                                responseObject.setData(UserManager.getUser(user.getUsername()));
                            } else {
                                System.out.println("User " + user.getUsername() + " has not been registered.");
                                responseObject.setStatusCode("300_REGISTER_US");
                                responseObject.setData(null);
                            }
                        } else if (requestObject.getStatusCode().equals("LOGIN")) {
                            String[] tuple = (String[]) requestObject.getData();
                            if (UserManager.loginUser(tuple[0], tuple[1])) {
                                System.out.println("User " + tuple[0] + " has been logged in.");
                                responseObject.setStatusCode("200_LOGIN_S");
                                responseObject.setData(UserManager.getUser(tuple[0]));
                            } else {
                                System.out.println("User " + tuple[0] + " has not been logged in.");
                                responseObject.setStatusCode("300_LOGIN_US");
                                responseObject.setData(null);
                            }
                        } else if (requestObject.getStatusCode().equals("JOINING")) {
                            String roomID = (String) requestObject.getData();
                            if (ChatSystem.getChat(roomID) != null) {
                                System.out.println("Room " + roomID + " has already been joined.");
                                responseObject.setStatusCode("200_JOIN_S");
                                responseObject.setData(ChatSystem.getChat(roomID));
                            } else {
                                System.out.println("Room " + roomID + " has not been joined.");
                                responseObject.setStatusCode("300_JOIN_S");
                                responseObject.setData(null);
                            }
                        } else if (requestObject.getStatusCode().equals("CREATING")) {
                            String roomID = (String) requestObject.getData();
                            if (ChatSystem.getChat(roomID) != null) {
                                System.out.println("Room " + roomID + " has already been created.");
                                responseObject.setStatusCode("200_CREATING_S");
                                responseObject.setData(ChatSystem.getChat(roomID));
                            } else {
                                System.out.println("Room " + roomID + " has not been created.");
                                responseObject.setStatusCode("300_CREATING_US");
                                responseObject.setData(null);
                            }
                        }

                        System.out.println(responseObject.getStatusCode());
                        outputStream.writeObject(responseObject);
                        outputStream.flush();
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error handling client request: " + e.getMessage());
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error with client connection: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}