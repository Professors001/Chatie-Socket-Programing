package Client;

import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            String request, response;
            System.out.println("Chat Client");
            System.out.println("Enter commands (REGISTER, LOGIN, CREATE_CHAT, SEND_MESSAGE) or type 'EXIT' to quit:");

            while ((request = userInput.readLine()) != null) {
                if (request.equalsIgnoreCase("EXIT")) {
                    System.out.println("Exiting...");
                    break;
                }

                if (request.trim().isEmpty()) {
                    System.out.println("Empty command. Please enter a valid command.");
                    continue;
                }

                out.println(request);
                response = in.readLine();
                if (response == null) {
                    System.out.println("Server connection lost.");
                    break;
                }
                System.out.println("Server response: " + response);
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}