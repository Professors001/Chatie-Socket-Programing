package Server;

import Server.Chatie.Class.*;
import Server.Chatie.Service.*;

import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    handleRequest(request);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleRequest(String request) {
            String[] parts = request.split(" ", 4);
            String command = parts[0];
            String response;

            try {
                switch (command) {
                    case "REGISTER":
                        if (parts.length < 3) {
                            response = "400 BAD_REQUEST REGISTER command requires username and password";
                            break;
                        }
                        String regUsername = parts[1];
                        String regPassword = parts[2];
                        boolean registered = UserManager.registerUser(regUsername, regPassword, "DefaultName");
                        response = registered ? "200 OK User registered" : "400 BAD_REQUEST Username already exists";
                        break;

                    case "LOGIN":
                        if (parts.length < 3) {
                            response = "400 BAD_REQUEST LOGIN command requires username and password";
                            break;
                        }
                        String loginUsername = parts[1];
                        String loginPassword = parts[2];
                        boolean loggedIn = UserManager.loginUser(loginUsername, loginPassword);
                        response = loggedIn ? "200 OK User logged in" : "401 UNAUTHORIZED Invalid credentials";
                        break;

                    case "CREATE_CHAT":
                        if (parts.length < 2) {
                            response = "400 BAD_REQUEST CREATE_CHAT command requires chatID";
                            break;
                        }
                        String chatID = parts[1];
                        boolean chatCreated = ChatSystem.newChat(chatID);
                        response = chatCreated ? "200 OK Chat created" : "400 BAD_REQUEST Chat already exists";
                        break;

                    case "SEND_MESSAGE":
                        if (parts.length < 4) {
                            response = "400 BAD_REQUEST SEND_MESSAGE command requires chatID, message, and senderID";
                            break;
                        }
                        chatID = parts[1];
                        String messageText = parts[2];
                        String senderID = parts[3];
                        Chat chat = ChatSystem.getChat(chatID);
                        if (chat != null) {
                            chat.addMessage(new Message(senderID, messageText));
                            response = "200 OK Message sent";
                        } else {
                            response = "404 NOT_FOUND Chat not found";
                        }
                        break;
                    default:
                        response = "400 BAD_REQUEST Unknown command";
                        break;
                }
            } catch (Exception e) {
                response = "500 INTERNAL_ERROR " + e.getMessage();
            }

            out.println(response);
        }
    }
}