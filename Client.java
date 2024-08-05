import Server.Chatie.Class.User;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            String serverAddress = "localhost";
            int port = 65432;
            String status = "VIRGIN";
            User loginUser = null;
            String roomID = null;

            // Connect to server
            Socket socket = new Socket(serverAddress, port);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();  // Ensure header is sent before ObjectInputStream is created
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            Response responseObject = null;

            System.out.println("Welcome to Chatie!");

            while (true) {
                System.out.println("STATUS CODE: " + status);
                responseObject = null;

                if (status.equals("VIRGIN")) {
                    System.out.println("Type \"1\" to login to your account or \"2\" to register.\"");
                    String command = scanner.nextLine();

                    if (command.equals("1")) {
                        status = "LOGIN";
                    } else if (command.equals("2")) {
                        status = "REGISTER";
                    } else {
                        System.out.println("Invalid command, please try again.");
                    }
                } else if (status.equals("LOGIN")) {
                    System.out.println("Please enter your account username");
                    String loginUsername = scanner.nextLine();

                    System.out.println("Please enter your account password");
                    String loginPassword = scanner.nextLine();

                    String[] tuple = {loginUsername, loginPassword};
                    Response requestObject = new Response(status, tuple);
                    outputStream.writeObject(requestObject);
                    outputStream.flush();  // Ensure data is sent

                    try {
                        responseObject = (Response) inputStream.readObject();
                        System.out.println("Response received: " + responseObject.getStatusCode());

                        if (responseObject.getStatusCode().equals("200_LOGIN_S")) {
                            System.out.println("You are now logged in");
                            loginUser = (User) responseObject.getData();
                            status = "LOBBY";
                        } else {
                            System.out.println("Invalid username or password, please try again.");
                            status = "VIRGIN";
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error handling client request: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (status.equals("REGISTER")) {
                    System.out.println("Please enter your account username");
                    String username = scanner.nextLine();

                    System.out.println("Please enter your account password");
                    String password = scanner.nextLine();

                    System.out.println("Please enter your account Display Name");
                    String displayName = scanner.nextLine();

                    User newUser = new User(username, password, displayName);
                    Response requestObject = new Response(status, newUser);
                    outputStream.writeObject(requestObject);
                    outputStream.flush();  // Ensure data is sent

                    try {
                        responseObject = (Response) inputStream.readObject();
                        System.out.println("Response received: " + responseObject.getStatusCode());

                        if (responseObject.getStatusCode().equals("200_REGISTER_S")) {
                            System.out.println("REGISTER SUCCESSFULLY");
                            System.out.println("You are now logged in");
                            loginUser = (User) responseObject.getData();
                            status = "LOBBY";
                        } else {
                            System.out.println("REGISTER FAILED: " + responseObject.getStatusCode());
                            status = "VIRGIN";
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error handling client request: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (status.equals("LOBBY")) {
                    System.out.println("Type \"1\" to Create a Chat Room \"2\" to join Chat room or \"3\" to logout.\"");
                    String command = scanner.nextLine();

                    if (command.equals("2")) {
                        status = "JOINING";
                    } else if (command.equals("3")) {
                        loginUser = null;
                        roomID = null;
                        status = "VIRGIN";
                    } else if (command.equals("1")) {
                        status = "CREATING";
                    } else {
                        System.out.println("Invalid command, please try again.");
                    }
                } else if (status.equals("JOINING")) {
                    System.out.println("Please enter Chat Room ID or Type \"LEAVE\" to go back to Lobby.\"");
                    String command = scanner.nextLine();

                    if (command.equals("LEAVE")) {
                        roomID = null;
                        status = "LOBBY";
                    } else {
                        roomID = command;
                        Response requestObject = new Response(status, roomID);
                        outputStream.writeObject(requestObject);
                        outputStream.flush();  // Ensure data is sent

                        try {
                            responseObject = (Response) inputStream.readObject();
                            System.out.println("Response received: " + responseObject.getStatusCode());

                            if (responseObject.getStatusCode().equals("200_JOIN_S")) {
                                status = "CHAT";
                            } else {
                                System.out.println("Invalid room ID, please try again.");
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Error handling client request: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } else if (status.equals("CREATING")) {
                    System.out.println("Please Enter the room ID");
                    roomID = scanner.nextLine();
                    Response requestObject = new Response(status, roomID);
                    outputStream.writeObject(requestObject);
                    outputStream.flush();  // Ensure data is sent

                    try {
                        responseObject = (Response) inputStream.readObject();
                        System.out.println("Response received: " + responseObject.getStatusCode());
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error handling client request: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                System.out.println("------------------------------------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}