import Server.Chatie.Class.Chat;
import Server.Chatie.Class.User;
import Server.Chatie.Service.ChatHandler;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             ObjectOutputStream ops = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ips = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in);) {

            String status = "VIRGIN";
            User loginUser = null;
            String chatID = null;

            System.out.println("Welcome to Chatie!");
            while (true) {
                // Create and send the request to the server
//                System.out.println("STATUS CODE: " + status); //debug

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
                    ops.writeObject(requestObject);
                    ops.flush();

                    try {
                        Response responseObject = (Response) ips.readObject();
//                        System.out.println("Response received: " + responseObject.getStatusCode()); //debug

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

                    String[] tuple = {username, password};
                    Response requestObject = new Response(status, tuple);
                    ops.writeObject(requestObject);
                    ops.flush();
                    try {
                        Response responseObject = (Response) ips.readObject();
//                        System.out.println("Response received: " + responseObject.getStatusCode()); //debug

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
                    System.out.println("Welcome: " + loginUser.getUsername());
                    System.out.println("Type \"1\" to Create a Chat Room \"2\" to join Chat room or \"3\" to logout.\"");
                    String command = scanner.nextLine();

                    if (command.equals("2")) {
                        status = "JOINING";
                    } else if (command.equals("3")) {
                        loginUser = null;
                        chatID = null;
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
                        chatID = null;
                        status = "LOBBY";
                    } else {
                        Response requestObject = new Response(status, command);
                        ops.writeObject(requestObject);
                        ops.flush();
                        try {
                            Response responseObject = (Response) ips.readObject();
//                            System.out.println("Response received: " + responseObject.getStatusCode()); //debug

                            if (responseObject.getStatusCode().equals("200_JOIN_S")) {
                                status = "CHAT";
                                chatID = command;
                            } else {
                                System.out.println("Invalid room ID, please try again.");
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Error handling client request: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } else if (status.equals("CREATING")) {
                    System.out.println("Please Enter the room ID or Type \"LEAVE\" to go back to Lobby.\"");
                    String newChatID = scanner.nextLine();

                    if (newChatID.equals("LEAVE")) {
                        status = "LOBBY";
                    } else {
                        Response requestObject = new Response(status, newChatID);
                        ops.writeObject(requestObject);
                        ops.flush();
                        try {
                            Response responseObject = (Response) ips.readObject();
//                            System.out.println("Response received: " + responseObject.getStatusCode()); //debug

                            if (responseObject.getStatusCode().equals("200_CREATING_S")) {
                                status = "CHAT";
                                chatID = newChatID;
                            } else {
                                System.out.println("Room ID Already been used, please try again.");
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Error handling client request: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                } else if (status.equals("CHAT")) {
                    Response requestObject = new Response("REQUEST_CHAT", chatID);
                    ops.writeObject(requestObject);
                    ops.flush();

                    try {
                        Response responseObject = (Response) ips.readObject();
//                        System.out.println("Response on received: " + responseObject.getStatusCode()); //debug
                        System.out.println("You are now CHAT in " + chatID + " Chat Room");

                        if (responseObject.getStatusCode().equals("200_CHAT_S")) {
                            Chat chat = (Chat) responseObject.getData();
                            ChatHandler.printChat(chat.getMessages());

                        } else {
                            System.out.println("Chat Unavailable, please try again.");
                            status = "LOBBY";
                        }

                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error handling client request: " + e.getMessage());
                        e.printStackTrace();
                    }

                    System.out.println("Please type a Message or Type \"LEAVE\" to go back to Lobby.\"");
                    String message = scanner.nextLine();

                    if (message.equals("LEAVE")) {
                        chatID = null;
                        status = "LOBBY";
                    } else {
                        String[] tuple = {chatID, loginUser.getUsername(), message};
                        Response sending = new Response("SENDING", tuple);
                        ops.writeObject(sending);
                        ops.flush();
                        try {
                            Response responseObject = (Response) ips.readObject();
//                            System.out.println("Response received: " + responseObject.getStatusCode()); //debug

                            if (responseObject.getStatusCode().equals("300_SENDING_US")) {
                                System.out.println("Cannot send message, please try again.");
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Error handling client request: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }

                System.out.println("------------------------------------------");

                // Simulate a short delay between requests
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}